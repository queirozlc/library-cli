package com.faesa.librarycli.core.registerpatron;

import java.math.BigDecimal;

public enum PatronType {
    STUDENT {
        @Override
        public Integer availableHoldDuration() {
            return 7;
        }

        @Override
        public BigDecimal currentHoldFee() {
            /// students by default pay the same as regular patrons, but this can be changed if some policy is implemented
            return BigDecimal.valueOf(0.5);
        }

        @Override
        public boolean maximumLoansExceeded(int size) {
            return size >= 5;
        }

        @Override
        public BigDecimal feeForOverdueLoan() {
            // students pay 10% of the book price for each day the book is overdue
            return BigDecimal.valueOf(0.1);
        }

        @Override
        public boolean maximumHoldsExceeded(int size) {
            return false;
        }
    },
    RESEARCHER {
        @Override
        public Integer availableHoldDuration() {
            return 0;
        }

        @Override
        public BigDecimal currentHoldFee() {
            // research does not pay fees
            return BigDecimal.ZERO;
        }

        @Override
        public boolean maximumLoansExceeded(int size) {
            return false;
        }

        @Override
        public BigDecimal feeForOverdueLoan() {
            // researchers pay 5% of the book price for each day the book is overdue
            return BigDecimal.valueOf(0.05);
        }

        @Override
        public boolean maximumHoldsExceeded(int size) {
            return false;
        }
    },
    REGULAR {
        @Override
        public Integer availableHoldDuration() {
            return 7;
        }

        @Override
        public BigDecimal currentHoldFee() {
            // regular patrons pay 5% of the book price to place a hold
            return BigDecimal.valueOf(0.5);
        }

        @Override
        public boolean maximumLoansExceeded(int size) {
            return size >= 5;
        }

        @Override
        public BigDecimal feeForOverdueLoan() {
            // regular patrons pay 10% of the book price for each day the book is overdue
            return BigDecimal.valueOf(0.1);
        }

        @Override
        public boolean maximumHoldsExceeded(int size) {
            return size >= 5;
        }
    };

    public static PatronType supports(String type) {
        return PatronType.valueOf(type.toUpperCase());
    }

    public abstract Integer availableHoldDuration();

    public abstract BigDecimal currentHoldFee();

    public abstract boolean maximumLoansExceeded(int size);


    public abstract BigDecimal feeForOverdueLoan();

    public abstract boolean maximumHoldsExceeded(int size);

}
