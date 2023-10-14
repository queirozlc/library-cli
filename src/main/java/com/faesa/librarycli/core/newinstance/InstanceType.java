package com.faesa.librarycli.core.newinstance;

import com.faesa.librarycli.core.registerpatron.Patron;

public enum InstanceType {

    FREE {
        @Override
        public boolean acceptsHold(Patron patron) {
            return true;
        }
    },
    RESTRICTED {
        @Override
        public boolean acceptsHold(Patron patron) {
            return patron.canBorrowRestrictedInstances();
        }
    };

    public static InstanceType supports(String value) {
        return InstanceType.valueOf(value.toUpperCase());
    }

    public abstract boolean acceptsHold(Patron patron);
}
