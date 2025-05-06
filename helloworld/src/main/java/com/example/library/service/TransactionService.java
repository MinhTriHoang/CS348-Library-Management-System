package com.example.library.service;

import org.springframework.transaction.annotation.Isolation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.library.model.Book;
import com.example.library.model.LibraryMember;
import com.example.library.model.Transaction;
import com.example.library.repository.BookRepository;
import com.example.library.repository.LibraryMemberRepository;
import com.example.library.repository.TransactionRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final LibraryMemberRepository libraryMemberRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, 
                             BookRepository bookRepository,
                             LibraryMemberRepository libraryMemberRepository) {
        this.transactionRepository = transactionRepository;
        this.bookRepository = bookRepository;
        this.libraryMemberRepository = libraryMemberRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    

    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Integer id) {
        return transactionRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
public List<Map<String, Object>> getTransactionReport(LocalDate startDate, LocalDate endDate, String status) {
    try {
        StringBuilder sqlBuilder = new StringBuilder();
        
        sqlBuilder.append("SELECT ")
                 .append("calculated_status, ")
                 .append("COUNT(transaction_id) as count, ")
                 .append("AVG(duration) as avgDuration ")
                 .append("FROM (")
                 .append("   SELECT t.transaction_id, ")
                 .append("   CASE ")
                 .append("     WHEN t.status = 'borrowed' AND t.expected_return_date < CURRENT_DATE() THEN 'overdue' ")
                 .append("     ELSE t.status ")
                 .append("   END as calculated_status, ")
                 .append("   CASE ")
                 .append("     WHEN t.status = 'borrowed' THEN DATEDIFF(CURRENT_DATE(), t.borrow_date) ")
                 .append("     ELSE DATEDIFF(t.actual_return_date, t.borrow_date) ")
                 .append("   END as duration ")
                 .append("   FROM transactions t ")
                 .append("   WHERE 1=1 ");
        
        if (startDate != null && endDate != null) {
            sqlBuilder.append("   AND t.borrow_date BETWEEN '").append(startDate).append("' AND '").append(endDate).append("' ");
        }
        
        // Handle status filtering in the inner query
        if (status != null && !status.isEmpty() && !status.equals("All Statuses")) {
            if (status.equalsIgnoreCase("overdue")) {
                sqlBuilder.append("   AND t.status = 'borrowed' AND t.expected_return_date < CURRENT_DATE() ");
            } else if (status.equalsIgnoreCase("borrowed")) {
                sqlBuilder.append("   AND t.status = 'borrowed' AND t.expected_return_date >= CURRENT_DATE() ");
            } else {
                sqlBuilder.append("   AND t.status = '").append(status.toLowerCase()).append("' ");
            }
        }
        
        sqlBuilder.append(") as subquery ")
                 .append("GROUP BY calculated_status");
        
        String sql = sqlBuilder.toString();
        System.out.println("Executing summary SQL: " + sql);
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        List<Map<String, Object>> reportData = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", row[0]);
            item.put("count", row[1]);
            item.put("avgDuration", row[2]);
            reportData.add(item);
        }
        
        return reportData;
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error in getTransactionReport: " + e.getMessage());
        return new ArrayList<>();
    }
}
    
    // Transaction with isolation level 
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction createTransaction(Integer memberId, Integer bookId, LocalDate borrowDate, 
                                       LocalDate expectedReturnDate) {
        Optional<LibraryMember> memberOpt = libraryMemberRepository.findById(memberId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        
        if (memberOpt.isPresent() && bookOpt.isPresent()) {
            LibraryMember member = memberOpt.get();
            Book book = bookOpt.get();
            
            if (book.getAvailableCopies() <= 0) {
                throw new RuntimeException("No available copies for book: " + book.getTitle());
            }
            
            
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
            

            Transaction transaction = new Transaction();
            transaction.setMember(member);
            transaction.setBook(book);
            transaction.setBorrowDate(borrowDate);
            transaction.setExpectedReturnDate(expectedReturnDate);
            transaction.setStatus("borrowed");
            
            return transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Member or Book not found");
        }
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction returnBook(Integer transactionId, LocalDate returnDate) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            Book book = transaction.getBook();
            
            // Update book available copies
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
            
            // Update transaction
            transaction.setActualReturnDate(returnDate);
            transaction.setStatus("returned");
            
            return transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Transaction not found");
        }
    }

    @Transactional(readOnly = true)
public List<Map<String, Object>> getDetailedTransactionReport(LocalDate startDate, LocalDate endDate, String status) {
    try {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT t.transaction_id, b.title, b.author, u.username, ")
                 .append("t.borrow_date, t.expected_return_date, t.actual_return_date, ")
                 .append("CASE ")
                 .append("  WHEN t.status = 'borrowed' AND t.expected_return_date < CURRENT_DATE() THEN 'overdue' ")
                 .append("  ELSE t.status ")
                 .append("END as effective_status, ")
                 .append("CASE ")
                 .append("  WHEN t.status = 'borrowed' THEN DATEDIFF(CURRENT_DATE(), t.borrow_date) ")
                 .append("  ELSE DATEDIFF(t.actual_return_date, t.borrow_date) ")
                 .append("END as duration ")
                 .append("FROM transactions t ")
                 .append("JOIN books b ON t.book_id = b.book_id ")
                 .append("JOIN users u ON t.user_id = u.user_id ")
                 .append("WHERE 1=1 ");
        
        if (startDate != null && endDate != null) {
            sqlBuilder.append("AND t.borrow_date BETWEEN '").append(startDate).append("' AND '").append(endDate).append("' ");
        }
        
        
        if (status != null && !status.isEmpty() && !status.equals("All Statuses")) {
            if (status.equalsIgnoreCase("overdue")) {
                sqlBuilder.append("AND t.status = 'borrowed' AND t.expected_return_date < CURRENT_DATE() ");
            } else if (status.equalsIgnoreCase("borrowed")) {
                sqlBuilder.append("AND t.status = 'borrowed' AND t.expected_return_date >= CURRENT_DATE() ");
            } else {
                sqlBuilder.append("AND t.status = '").append(status.toLowerCase()).append("' ");
            }
        }
        
        sqlBuilder.append("ORDER BY t.borrow_date DESC");
        
        String sql = sqlBuilder.toString();
        System.out.println("Executing detailed SQL: " + sql);
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        System.out.println("Found " + results.size() + " transactions");
        
        List<Map<String, Object>> detailedReport = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("transactionId", row[0]);
            item.put("bookTitle", row[1]);
            item.put("author", row[2]);
            item.put("username", row[3]);
            item.put("borrowDate", row[4]);
            item.put("expectedReturnDate", row[5]);
            item.put("actualReturnDate", row[6]);
            item.put("status", row[7]); 
            item.put("duration", row[8]);
            detailedReport.add(item);
        }
        
        return detailedReport;
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error in getDetailedTransactionReport: " + e.getMessage());
        return new ArrayList<>();
    }
}

    @Transactional(readOnly = true)
    public List<Object[]> getOverdueBooks() {
        return transactionRepository.findOverdueBooks();
    }
}