package com.example.library.controller;


import com.example.library.model.Book;
import com.example.library.model.Category;
import com.example.library.service.BookService;
import com.example.library.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    
    @Autowired
    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }
    
    @GetMapping
    public String listBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books/list";
    }
    
    @GetMapping("/new")
    public String showNewBookForm(Model model) {
       
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categories);
        return "books/form";
    }
    
    @PostMapping("/save")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book saved successfully!");
        return "redirect:/books";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Integer id, Model model) {
        Optional<Book> bookOpt = bookService.getBookById(id);
        
        if (bookOpt.isPresent()) {
            model.addAttribute("book", bookOpt.get());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "books/form";
        } else {
            return "redirect:/books";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Book> bookOpt = bookService.getBookById(id);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                bookService.deleteBook(id);
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Book '" + book.getTitle() + "' was deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Book not found or has already been deleted by another user.");
            }
        } catch (EmptyResultDataAccessException e) {
            
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Book not found or has already been deleted by another user.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "An error occurred while deleting the book: " + e.getMessage());
        }
    
        return "redirect:/books";
    }
    
    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String title,
                             @RequestParam(required = false) String author,
                             @RequestParam(required = false) Integer categoryId,
                             @RequestParam(required = false) Integer publishedYear,
                             Model model) {
        List<Book> searchResults = bookService.findBooksByFilters(title, author, categoryId, publishedYear);
        model.addAttribute("books", searchResults);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", title);
        model.addAttribute("author", author);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("publishedYear", publishedYear);
        return "books/list";
    }


    
}