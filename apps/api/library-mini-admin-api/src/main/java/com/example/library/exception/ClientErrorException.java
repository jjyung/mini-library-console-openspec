package com.example.library.exception;

import com.example.library.api.BusinessCode;

public class ClientErrorException extends RuntimeException {

    private final BusinessCode businessCode;

    public ClientErrorException(String message) {
        this(BusinessCode.CLIENT_ERROR, message);
    }

    public ClientErrorException(BusinessCode businessCode, String message) {
        super(message);
        this.businessCode = businessCode;
    }

    public BusinessCode getBusinessCode() {
        return this.businessCode;
    }
}
