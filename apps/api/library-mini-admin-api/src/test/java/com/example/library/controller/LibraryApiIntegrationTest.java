package com.example.library.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LibraryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateBookSearchBorrowAndReturnByIsbn() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Clean Code",
                      "isbn": "978-0-13-235088-4",
                      "author": "Robert C. Martin",
                      "category": "technology",
                      "quantity": 2,
                      "active": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.book.isbn").value("978-0-13-235088-4"));

        this.mockMvc.perform(get("/api/books").param("keyword", "Clean"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.items", hasSize(1)))
            .andExpect(jsonPath("$.data.items[0].availableCount").value(2));

        this.mockMvc.perform(post("/api/loans/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "isbn": "978-0-13-235088-4",
                      "readerId": "samson"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.book.availableCount").value(1));

        this.mockMvc.perform(post("/api/loans/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "isbn": "978-0-13-235088-4"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.book.availableCount").value(2));
    }

    @Test
    void shouldReturnSpecificBusinessCodesForClientErrors() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": " ",
                      "isbn": "978-0-13-235088-4",
                      "author": "Author",
                      "category": "technology",
                      "quantity": 0,
                      "active": true
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0400"))
            .andExpect(jsonPath("$.data").doesNotExist());

        this.mockMvc.perform(post("/api/loans/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "isbn": "978-9-99-999999-9",
                      "readerId": "samson"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0404"))
            .andExpect(jsonPath("$.message").value("找不到該 ISBN 的書籍"));
    }
}
