package com.runforge.service;

import com.runforge.model.Member;
import com.runforge.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public List<Member> findActive() {
        return memberRepository.findByActiveTrue();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mitglied nicht gefunden: " + id));
    }

    public Member create(Member member) {
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new RuntimeException("E-Mail bereits registriert: " + member.getEmail());
        }
        return memberRepository.save(member);
    }

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

    public void delete(Long id) {
        Member member = findById(id);
        member.setActive(false); // Soft delete
        memberRepository.save(member);
    }
}
