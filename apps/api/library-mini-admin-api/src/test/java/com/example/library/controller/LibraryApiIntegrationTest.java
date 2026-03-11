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
    void shouldCreateBookAndListIt() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Clean Code",
                      "isbn": "9780132350884",
                      "author": "Robert C. Martin",
                      "category": "technology",
                      "active": true,
                      "initialCopies": 2
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.book.title").value("Clean Code"))
            .andExpect(jsonPath("$.data.book.isbn").value("9780132350884"))
            .andExpect(jsonPath("$.data.book.status").value("AVAILABLE"));

        this.mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.books", hasSize(1)))
            .andExpect(jsonPath("$.data.books[0].availableCopies").value(2))
            .andExpect(jsonPath("$.data.books[0].category").value("technology"));
    }

    @Test
    void shouldReturnBusinessCodeForInvalidCreateRequest() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": " ",
                      "isbn": "9780132350884",
                      "author": "Author",
                      "category": "technology",
                      "active": true,
                      "initialCopies": 0
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0000"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnBusinessCodeWhenCheckoutWithoutAvailableCopies() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Domain-Driven Design",
                      "isbn": "9780321125217",
                      "author": "Eric Evans",
                      "category": "technology",
                      "active": true,
                      "initialCopies": 1
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"));

        this.mockMvc.perform(post("/api/transactions/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "isbn": "9780321125217",
                      "readerId": "Samson"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"));

        this.mockMvc.perform(post("/api/transactions/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "isbn": "9780321125217",
                      "readerId": "Taylor"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0000"))
            .andExpect(jsonPath("$.message").value("No available copies for checkout"));
    }
}
