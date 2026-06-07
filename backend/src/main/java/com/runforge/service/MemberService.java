package com.runforge.service;

import com.runforge.model.Member;
import com.runforge.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MemberService — Business logic for managing running group members.
 *
 * <p>This service provides CRUD operations for members with additional features:
 * <ul>
 *   <li>Retrieval of active members only (where active = true)</li>
 *   <li>Email uniqueness validation on creation</li>
 *   <li>Soft deletion (sets active flag to false)</li>
 *   <li>Transactional support for data consistency</li>
 * </ul>
 *
 * <p>All methods use {@link MemberRepository} for data access and throw
 * {@link RuntimeException} when entities are not found or business rules are violated.
 *
 * @see Member
 * @see MemberRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * Retrieves all members (including inactive ones).
     *
     * @return list of all members
     */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * Retrieves only active members (where active = true).
     *
     * @return list of active members
     */
    public List<Member> findActive() {
        return memberRepository.findByActiveTrue();
    }

    /**
     * Retrieves a specific member by ID.
     *
     * @param id the member's unique identifier
     * @return the member if found
     * @throws RuntimeException if member not found
     */
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mitglied nicht gefunden: " + id));
    }

    /**
     * Creates a new member with validation.
     *
     * @param member the member to create (firstName, lastName, email, phone, level required)
     * @return the created member with auto-generated ID and joinedDate
     * @throws RuntimeException if email is already registered
     */
    public Member create(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("E-Mail bereits registriert: " + member.getEmail());
        }
        return memberRepository.save(member);
    }

    /**
     * Updates an existing member by ID.
     *
     * <p>Merges provided fields into the existing member record. All fields are updatable.
     *
     * @param id the member's ID (must exist)
     * @param updated the member dto with fields to update
     * @return the updated member
     * @throws RuntimeException if member not found
     */
    public Member update(Long id, Member updated) {
        Member existing = findById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setLevel(updated.getLevel());
        existing.setActive(updated.isActive());
        return memberRepository.save(existing);
    }

    /**
     * Soft-deletes a member by setting active = false.
     *
     * <p>The member record remains in the database for audit purposes but is
     * excluded from standard queries via {@link #findActive()}.
     *
     * @param id the member's ID
     * @throws RuntimeException if member not found
     */
    public void delete(Long id) {
        Member member = findById(id);
        member.setActive(false); // Soft delete
        memberRepository.save(member);
    }
}
