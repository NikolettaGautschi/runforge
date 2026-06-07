package com.runforge.repository;

import com.runforge.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * MemberRepository — Data access layer for Member entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations plus
 * custom queries for member-specific operations:
 * <ul>
 *   <li>Finding active members only (where active = true)</li>
 *   <li>Finding members by running level</li>
 *   <li>Checking email uniqueness</li>
 *   <li>Counting active members and level distribution</li>
 * </ul>
 *
 * @see Member
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * Finds all active members (where active = true).
     *
     * @return list of active members
     */
    List<Member> findByActiveTrue();

    /**
     * Finds all members with a specific running level.
     *
     * @param level the running level to filter by
     * @return list of members at the given level
     */
    List<Member> findByLevel(Member.RunningLevel level);

    /**
     * Checks if an email address is already registered.
     *
     * <p>Used to enforce unique email constraint during member creation.
     *
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Counts active members (where active = true).
     *
     * @return count of active members
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.active = true")
    long countActive();

    /**
     * Counts active members grouped by running level.
     *
     * <p>Returns an array for each level with [level_name, count].
     * Used to populate membersByLevel distribution on the dashboard.
     *
     * @return list of Object[] arrays: {level, count}
     */
    @Query("SELECT m.level, COUNT(m) FROM Member m WHERE m.active = true GROUP BY m.level")
    List<Object[]> countByLevel();
}
