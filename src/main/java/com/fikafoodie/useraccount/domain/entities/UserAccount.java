package com.fikafoodie.useraccount.domain.entities;

import lombok.Data;

@Data
public class UserAccount {

    private Id id = new Id("1");
    private Credits credits = new Credits(0);

    public void addCredits(Credits credits) {
        this.credits = this.credits.add(credits);
    }

    public void subtractCredits(Credits credits) {
        this.credits = this.credits.subtract(credits);
    }

    public Credits creditBalance() {
        return credits;
    }

    public record Id(String value) {
        public Id {
            if (value == null) {
                throw new IllegalArgumentException("User account id cannot be null");
            }
        }
    }

    public record Credits(int value) implements Comparable<Credits> {
        public Credits {
            if (value < 0) {
                throw new IllegalArgumentException("Credits cannot be negative");
            }
        }

        public Credits add(Credits credits) {
            return new Credits(this.value + credits.value);
        }

        public Credits subtract(Credits credits) {
            return this.value - credits.value < 0 ? new Credits(0) : new Credits(this.value - credits.value);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Credits credits && credits.value == this.value;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(value);
        }

        @Override
        public int compareTo(Credits other) {
            return Integer.compare(this.value, other.value);
        }
    }
}
