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
 *   - Product: Uses Money for pricing
 *   - Order: Uses Money for totals and amounts
 */
package org.acmsl.bytehot.examples.ecommerce.domain;

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
     * Creates a new Money instance from double.
     * @param amount The monetary amount
     * @param currency The currency code
     */
    public Money(final double amount, final String currency) {
        this(BigDecimal.valueOf(amount), currency);
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
     * Creates a Money instance from string amount.
     * @param amount The amount as string
     * @param currency The currency code
     * @return Money instance
     */
    public static Money of(final String amount, final String currency) {
        return new Money(new BigDecimal(amount), currency);
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
     * Multiplies by a factor.
     * @param factor The multiplication factor
     * @return New money instance with product
     */
    public Money multiply(final double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    /**
     * Multiplies by an integer factor.
     * @param factor The multiplication factor
     * @return New money instance with product
     */
    public Money multiply(final int factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    /**
     * Divides by a factor.
     * @param divisor The division factor
     * @return New money instance with quotient
     */
    public Money divide(final BigDecimal divisor) {
        return new Money(amount.divide(divisor, 2, RoundingMode.HALF_UP), currency);
    }

    /**
     * Divides by a factor.
     * @param divisor The division factor
     * @return New money instance with quotient
     */
    public Money divide(final double divisor) {
        return divide(BigDecimal.valueOf(divisor));
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

    /**
     * Compares this money amount with another.
     * @param other The other money amount
     * @return negative if less, zero if equal, positive if greater
     * @throws IllegalArgumentException if currencies don't match
     */
    public int compareTo(final Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare different currencies");
        }
        return amount.compareTo(other.amount);
    }

    /**
     * Checks if this amount is greater than another.
     * @param other The other money amount
     * @return true if greater, false otherwise
     */
    public boolean isGreaterThan(final Money other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if this amount is less than another.
     * @param other The other money amount
     * @return true if less, false otherwise
     */
    public boolean isLessThan(final Money other) {
        return compareTo(other) < 0;
    }

    /**
     * Gets the absolute value.
     * @return Money instance with absolute amount
     */
    public Money abs() {
        return new Money(amount.abs(), currency);
    }

    /**
     * Negates the amount.
     * @return Money instance with negated amount
     */
    public Money negate() {
        return new Money(amount.negate(), currency);
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