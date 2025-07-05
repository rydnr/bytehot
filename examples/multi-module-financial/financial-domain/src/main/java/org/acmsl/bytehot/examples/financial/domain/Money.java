/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: Money.java
 *
 * Author: Claude Code
 *
 * Class name: Money
 *
 * Responsibilities:
 *   - Represent monetary amounts with currency information
 *   - Provide arithmetic operations respecting currency constraints
 *   - Ensure immutability and value object semantics
 *
 * Collaborators:
 *   - Transaction: Uses Money for amounts and fees
 */
package org.acmsl.bytehot.examples.financial.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Money value object representing monetary amounts with currency.
 * @author Claude Code
 * @since 2025-07-04
 */
public class Money {

    /**
     * Monetary amount.
     */
    @NotNull
    private final BigDecimal amount;

    /**
     * Currency code.
     */
    @NotBlank
    private final String currency;

    /**
     * Creates a new Money instance.
     * @param amount The monetary amount
     * @param currency The currency code
     */
    public Money(final BigDecimal amount, final String currency) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }

    /**
     * Creates a zero amount in the given currency.
     * @param currency The currency code
     * @return Zero money instance
     */
    public static Money zero(final String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Adds another money amount.
     * @param other The other money amount
     * @return New money instance with sum
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money add(final Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Subtracts another money amount.
     * @param other The other money amount
     * @return New money instance with difference
     * @throws IllegalArgumentException if currencies don't match
     */
    public Money subtract(final Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract different currencies");
        }
        return new Money(amount.subtract(other.amount), currency);
    }

    /**
     * Multiplies by a factor.
     * @param factor The multiplication factor
     * @return New money instance with product
     */
    public Money multiply(final BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    /**
     * Checks if this amount is positive.
     * @return true if positive, false otherwise
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Checks if this amount is zero.
     * @return true if zero, false otherwise
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Checks if this amount is negative.
     * @return true if negative, false otherwise
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}