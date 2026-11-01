package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    public BookController(BookService bookService) { this.bookService = bookService; }

    @GetMapping public List<Book> getBooks() { return bookService.findAll(); }
    @GetMapping("/{id}") public Book getBook(@PathVariable Long id) { return bookService.findById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@Valid @RequestBody Book book) { return bookService.create(book); }
    @PutMapping("/{id}") public Book updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        return bookService.update(id, book);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) { bookService.delete(id); }
}
