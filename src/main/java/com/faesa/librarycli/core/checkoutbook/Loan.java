package com.faesa.librarycli.core.checkoutbook;

import com.faesa.librarycli.core.placinghold.Hold;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Loan {

    private final Hold hold;
    private Long id;
    private Integer time;

    private Instant loanDate;

    private Instant returnDate;

    private BigDecimal overdueFine;

    /**
     * <p>
     * Loan's are built by held books, so the constructor receives a current hold.
     * </p>
     *
     * @param hold The instance of the book that is being held.
     * @param time The time in days that the book will be borrowed.
     */
    public Loan(Hold hold, Integer time) {
        this.time = time;
        this.hold = hold;
        this.loanDate = Instant.now();
        this.returnDate = Instant.now().plus(time, ChronoUnit.DAYS);
        this.overdueFine = BigDecimal.ZERO;
    }
}
