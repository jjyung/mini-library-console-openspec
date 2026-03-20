package com.example.library.api;

public enum BusinessCode {
    SUCCESS("00000"),
    CLIENT_ERROR("A0000"),
    VALIDATION_ERROR("A0400"),
    BOOK_NOT_FOUND("A0404"),
    BOOK_ALREADY_EXISTS("A0409"),
    BOOK_NOT_BORROWABLE("A0410"),
    BOOK_NOT_RETURNABLE("A0411"),
    SYSTEM_ERROR("B0000"),
    THIRD_PARTY_ERROR("C0000");

    private final String code;

    BusinessCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
