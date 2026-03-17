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
                      "author": "Robert C. Martin",
                      "initialCopies": 2
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.book.title").value("Clean Code"));

        this.mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"))
            .andExpect(jsonPath("$.data.books", hasSize(1)))
            .andExpect(jsonPath("$.data.books[0].availableCopies").value(2));
    }

    @Test
    void shouldReturnBusinessCodeForInvalidCreateRequest() throws Exception {
        this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": " ",
                      "author": "Author",
                      "initialCopies": 0
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0000"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldReturnBusinessCodeWhenCheckoutWithoutAvailableCopies() throws Exception {
        String createResponse = this.mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "Domain-Driven Design",
                      "author": "Eric Evans",
                      "initialCopies": 1
                    }
                    """))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String bookId = createResponse.split("\"bookId\":\"")[1].split("\"")[0];

        this.mockMvc.perform(post("/api/transactions/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "bookId": "%s",
                      "borrowerName": "Samson"
                    }
                    """.formatted(bookId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("00000"));

        this.mockMvc.perform(post("/api/transactions/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "bookId": "%s",
                      "borrowerName": "Taylor"
                    }
                    """.formatted(bookId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0000"))
            .andExpect(jsonPath("$.message").value("No available copies for checkout"));
    }

    @Test
    void shouldReturnBusinessCodeWhenReturningUnknownTransaction() throws Exception {
        this.mockMvc.perform(post("/api/transactions/{transactionId}/return", "missing-transaction"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("A0000"))
            .andExpect(jsonPath("$.message").value("Transaction not found"));
    }
}
