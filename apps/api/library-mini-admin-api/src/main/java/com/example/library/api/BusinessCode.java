package com.example.library.api;

public enum BusinessCode {
    SUCCESS("00000"),
    CLIENT_ERROR("A0000"),
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
