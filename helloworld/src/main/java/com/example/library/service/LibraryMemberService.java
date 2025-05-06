package com.example.library.service;

import com.example.library.model.LibraryMember;
import com.example.library.repository.LibraryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryMemberService {
    private final LibraryMemberRepository libraryMemberRepository;
    
    @Autowired
    public LibraryMemberService(LibraryMemberRepository libraryMemberRepository) {
        this.libraryMemberRepository = libraryMemberRepository;
    }
    
    @Transactional(readOnly = true)
    public List<LibraryMember> getAllMembers() {
        return libraryMemberRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<LibraryMember> getMemberById(Integer id) {
        return libraryMemberRepository.findById(id);
    }
    
    @Transactional
    public LibraryMember saveMember(LibraryMember member) {
        return libraryMemberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Integer id) {
        libraryMemberRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<LibraryMember> findByUsername(String username) {
        return libraryMemberRepository.findByUsername(username);
    }
}