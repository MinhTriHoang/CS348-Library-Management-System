package com.example.library.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.transaction.annotation.Isolation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;

@Service
public class BookService {
    private final BookRepository bookRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Integer id) {
        return bookRepository.findById(id);
    }
    
    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteBook(Integer id) {
        
        System.out.println("Starting transaction to delete book ID: " + id);
    
    
        Optional<Book> bookOpt = bookRepository.findById(id);
    
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
        
        
            if (book.getTransactions() != null && 
                book.getTransactions().stream().anyMatch(t -> "borrowed".equals(t.getStatus()))) {
                throw new RuntimeException("Cannot delete book because it has active loans");
            }
        
            
            bookRepository.deleteById(id);
            System.out.println("Book deleted successfully: " + book.getTitle());
        } else {
            throw new RuntimeException("Book not found with ID: " + id);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Book> findBooksByFilters(String title, String author, Integer categoryId, Integer publishedYear) {
        StringBuilder queryBuilder = new StringBuilder("SELECT b FROM Book b WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();
        
        if (title != null && !title.isEmpty()) {
            queryBuilder.append(" AND b.title LIKE :title");
            parameters.put("title", "%" + title + "%");
        }
        
        if (author != null && !author.isEmpty()) {
            queryBuilder.append(" AND b.author LIKE :author");
            parameters.put("author", "%" + author + "%");
        }
        
        if (categoryId != null) {
            queryBuilder.append(" AND b.category.categoryId = :categoryId");
            parameters.put("categoryId", categoryId);
        }
        
        if (publishedYear != null) {
            queryBuilder.append(" AND b.publishedYear = :publishedYear");
            parameters.put("publishedYear", publishedYear);
        }
        
        TypedQuery<Book> query = entityManager.createQuery(queryBuilder.toString(), Book.class);
        parameters.forEach(query::setParameter);
        
        return query.getResultList();
    }
    
   
    @Transactional
    public void borrowBook(Integer bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                bookRepository.save(book);
            } else {
                throw new RuntimeException("No available copies for book: " + book.getTitle());
            }
        } else {
            throw new RuntimeException("Book not found with ID: " + bookId);
        }
    }
}