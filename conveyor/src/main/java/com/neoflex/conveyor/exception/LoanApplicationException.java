package com.neoflex.conveyor.exception;

import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class LoanApplicationException extends RuntimeException {
    public LoanApplicationException(String message) {
        super(message);
    }

}
