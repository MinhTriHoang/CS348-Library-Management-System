package com.example.library.repository;

import com.example.library.model.LibraryMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryMemberRepository extends JpaRepository<LibraryMember, Integer> {
    Optional<LibraryMember> findByUsername(String username);
    Optional<LibraryMember> findByEmail(String email);
}