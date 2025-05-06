package com.example.library.repository;

import com.example.library.model.Book;
import com.example.library.model.LibraryMember;
import com.example.library.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByMember(LibraryMember member);
    List<Transaction> findByBook(Book book);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByBorrowDateBetween(LocalDate start, LocalDate end);
    List<Transaction> findByExpectedReturnDateBefore(LocalDate date);
    @Query(value = "CALL GetOverdueBooks()", nativeQuery = true)
    List<Object[]> findOverdueBooks();
}