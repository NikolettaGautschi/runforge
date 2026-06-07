package com.runforge.controller;

import com.runforge.model.Member;
import com.runforge.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MemberController — REST endpoints for member management.
 *
 * <p>Provides CRUD operations for members via HTTP:
 * <ul>
 *   <li>GET /api/members — retrieve all members</li>
 *   <li>GET /api/members/active — retrieve active members only</li>
 *   <li>GET /api/members/{id} — retrieve a specific member</li>
 *   <li>POST /api/members — create new member</li>
 *   <li>PUT /api/members/{id} — update existing member</li>
 *   <li>DELETE /api/members/{id} — soft-delete member</li>
 * </ul>
 *
 * <p>CORS is enabled for http://localhost:3000 to support frontend proxying.
 * All request bodies are validated using Jakarta Validation annotations.
 *
 * @see MemberService
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {

    private final MemberService memberService;

    /**
     * GET /api/members — Retrieve all members.
     *
     * @return list of all members (including inactive)
     */
    @GetMapping
    public List<Member> getAll() {
        return memberService.findAll();
    }

    /**
     * GET /api/members/active — Retrieve active members only.
     *
     * @return list of active members (where active = true)
     */
    @GetMapping("/active")
    public List<Member> getActive() {
        return memberService.findActive();
    }

    /**
     * GET /api/members/{id} — Retrieve a specific member by ID.
     *
     * @param id the member's unique identifier
     * @return 200 OK with member data, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    /**
     * POST /api/members — Create a new member.
     *
     * @param member the member dto (firstName, lastName, email required; valid email format required)
     * @return 201 CREATED with the created member including auto-generated ID
     */
    @PostMapping
    public ResponseEntity<Member> create(@Valid @RequestBody Member member) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(member));
    }

    /**
     * PUT /api/members/{id} — Update an existing member.
     *
     * @param id the member's ID (must exist)
     * @param member the member dto with fields to update
     * @return 200 OK with the updated member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> update(@PathVariable Long id, @Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.update(id, member));
    }

    /**
     * DELETE /api/members/{id} — Soft-delete a member.
     *
     * <p>Sets the member's active flag to false. The record remains in the database.
     *
     * @param id the member's ID
     * @return 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
