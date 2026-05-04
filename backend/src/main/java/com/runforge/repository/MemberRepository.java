package com.runforge.repository;

import com.runforge.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByActiveTrue();

    List<Member> findByLevel(Member.RunningLevel level);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.active = true")
    long countActive();

    @Query("SELECT m.level, COUNT(m) FROM Member m WHERE m.active = true GROUP BY m.level")
    List<Object[]> countByLevel();
}
