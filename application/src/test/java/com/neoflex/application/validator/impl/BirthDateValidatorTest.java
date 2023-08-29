package com.neoflex.application.validator.impl;

import com.neoflex.application.validator.BirthDate;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BirthDateValidatorTest {
    private BirthDateValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        validator = new BirthDateValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void initialize() {
        BirthDate annotation = mock(BirthDate.class);

        assertDoesNotThrow(() -> validator.initialize(annotation));
    }

    @Test
    void isValid() {
        assertTrue(validator.isValid(LocalDate.of(1990, 1, 1), context));
    }

    @Test
    void testNullBirthDate() {
        assertFalse(validator.isValid(null, context));
    }
}