package com.example.library.repository;

import com.example.library.model.Book;
import com.example.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByCategory(Category category);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByPublishedYear(Integer year);
}