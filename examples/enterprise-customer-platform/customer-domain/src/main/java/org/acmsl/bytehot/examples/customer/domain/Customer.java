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
 * Filename: Customer.java
 *
 * Author: Claude Code
 *
 * Class name: Customer
 *
 * Responsibilities:
 *   - Act as aggregate root for customer lifecycle management
 *   - Enforce complex business rules for segmentation and compliance
 *   - Provide hot-swappable risk assessment and scoring algorithms
 *   - Handle customer journey progression and state transitions
 *
 * Collaborators:
 *   - CustomerRegistrationRequestedEvent: Incoming registration event
 *   - CustomerRegisteredEvent: Successful registration response
 *   - CustomerSegmentation: Value object for customer categorization
 *   - RiskProfile: Value object for risk assessment
 *   - ComplianceStatus: Value object for regulatory compliance
 */
package org.acmsl.bytehot.examples.customer.domain;

import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Customer aggregate root for enterprise customer management.
 * Demonstrates sophisticated business logic suitable for hot-swapping.
 * @author Claude Code
 * @since 2025-07-05
 */
public class Customer {

    /**
     * Unique customer identifier.
     */
    @NotNull
    private final String customerId;

    /**
     * Customer's first name.
     */
    @NotBlank
    private String firstName;

    /**
     * Customer's last name.
     */
    @NotBlank
    private String lastName;

    /**
     * Customer's email address.
     */
    @Email
    @NotNull
    private String email;

    /**
     * Customer's phone number.
     */
    private String phoneNumber;

    /**
     * Customer's date of birth.
     */
    @Past
    private LocalDate dateOfBirth;

    /**
     * Customer's primary address.
     */
    @NotNull
    private Address primaryAddress;

    /**
     * Customer's current status in the lifecycle.
     */
    @NotNull
    private CustomerStatus status;

    /**
     * Customer's market segment classification.
     */
    @NotNull
    private CustomerSegmentation segmentation;

    /**
     * Customer's risk assessment profile.
     */
    @NotNull
    private RiskProfile riskProfile;

    /**
     * Customer's regulatory compliance status.
     */
    @NotNull
    private ComplianceStatus complianceStatus;

    /**
     * Customer's lifetime value score.
     */
    private BigDecimal lifetimeValue;

    /**
     * Customer's credit score.
     */
    private Integer creditScore;

    /**
     * When the customer was registered.
     */
    @NotNull
    private final LocalDateTime registeredAt;

    /**
     * When the customer was last updated.
     */
    @NotNull
    private LocalDateTime lastUpdatedAt;

    /**
     * Version for optimistic locking.
     */
    private Long version;

    /**
     * Creates a new Customer aggregate.
     * @param firstName Customer's first name
     * @param lastName Customer's last name
     * @param email Customer's email
     * @param dateOfBirth Customer's date of birth
     * @param primaryAddress Customer's address
     */
    public Customer(final String firstName,
                   final String lastName,
                   final String email,
                   final LocalDate dateOfBirth,
                   final Address primaryAddress) {
        this.customerId = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
        this.status = CustomerStatus.PENDING;
        this.registeredAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
        this.version = 1L;
        
        // Initialize with default values (can be hot-swapped)
        this.segmentation = determineInitialSegmentation();
        this.riskProfile = performInitialRiskAssessment();
        this.complianceStatus = performInitialComplianceCheck();
        this.lifetimeValue = BigDecimal.ZERO;
    }

    /**
     * Primary port: accepts CustomerRegistrationRequestedEvent and processes it.
     * This method can be hot-swapped to change registration workflow.
     * @param event The incoming customer registration request event
     * @return List of resulting domain events
     */
    public static List<DomainResponseEvent<CustomerRegistrationRequestedEvent>> accept(
            final CustomerRegistrationRequestedEvent event) {
        List<DomainResponseEvent<CustomerRegistrationRequestedEvent>> results = new ArrayList<>();
        
        try {
            // Create customer from event (can be hot-swapped)
            Customer customer = createFromEvent(event);
            
            // Validate customer data (can be hot-swapped)
            if (!customer.isValid()) {
                results.add(new CustomerRegistrationRejectedEvent(event, "Customer validation failed"));
                return results;
            }
            
            // Check for duplicates (can be hot-swapped)
            if (!customer.passesDeduplicationCheck()) {
                results.add(new CustomerRegistrationRejectedEvent(event, "Duplicate customer detected"));
                return results;
            }
            
            // Perform comprehensive risk assessment (can be hot-swapped)
            customer.performComprehensiveRiskAssessment();
            
            // Apply compliance rules (can be hot-swapped)
            if (!customer.meetsComplianceRequirements()) {
                results.add(new CustomerRegistrationRejectedEvent(event, "Compliance requirements not met"));
                return results;
            }
            
            // Determine final segmentation (can be hot-swapped)
            customer.finalizeSegmentation();
            
            // Calculate initial lifetime value (can be hot-swapped)
            customer.calculateInitialLifetimeValue();
            
            // Activate customer if eligible (can be hot-swapped)
            if (customer.isEligibleForAutoActivation()) {
                customer.activate();
            }
            
            results.add(new CustomerRegisteredEvent(event, customer));
            
        } catch (Exception e) {
            results.add(new CustomerRegistrationRejectedEvent(event, "Registration error: " + e.getMessage()));
        }
        
        return results;
    }

    /**
     * Creates customer from domain event.
     * This method can be hot-swapped to change event mapping.
     * @param event The customer registration requested event
     * @return New customer instance
     */
    protected static Customer createFromEvent(final CustomerRegistrationRequestedEvent event) {
        return new Customer(
            event.getFirstName(),
            event.getLastName(),
            event.getEmail(),
            event.getDateOfBirth(),
            event.getPrimaryAddress()
        );
    }

    /**
     * Validates customer data according to business rules.
     * This method can be hot-swapped to change validation logic.
     * @return true if valid, false otherwise
     */
    protected boolean isValid() {
        // Basic validation (can be hot-swapped)
        if (firstName == null || firstName.trim().isEmpty()) {
            return false;
        }
        
        if (lastName == null || lastName.trim().isEmpty()) {
            return false;
        }
        
        if (email == null || !isValidEmail(email)) {
            return false;
        }
        
        if (dateOfBirth == null) {
            return false;
        }
        
        // Age validation (can be hot-swapped)
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18 || age > 120) {
            return false;
        }
        
        // Address validation (can be hot-swapped)
        if (primaryAddress == null || !primaryAddress.isValid()) {
            return false;
        }
        
        return true;
    }

    /**
     * Validates email format.
     * This method can be hot-swapped to change email validation rules.
     * @param email The email to validate
     * @return true if valid email, false otherwise
     */
    protected boolean isValidEmail(final String email) {
        // Simple email validation (can be hot-swapped)
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Checks for duplicate customers in the system.
     * This method can be hot-swapped to change deduplication logic.
     * @return true if no duplicates found, false otherwise
     */
    protected boolean passesDeduplicationCheck() {
        // Deduplication logic (can be hot-swapped)
        // In real implementation, this would check against existing customers
        // For demo purposes, we'll implement simple rules
        
        // Check for exact email match (can be hot-swapped)
        // This would typically use a repository or service
        
        // Check for similar names with same address (can be hot-swapped)
        // Advanced fuzzy matching could be implemented here
        
        return true; // Simplified for demo
    }

    /**
     * Determines initial customer segmentation.
     * This method can be hot-swapped to change segmentation logic.
     * @return Initial customer segmentation
     */
    protected CustomerSegmentation determineInitialSegmentation() {
        // Initial segmentation based on registration data (can be hot-swapped)
        if (primaryAddress != null && primaryAddress.isHighValueArea()) {
            return CustomerSegmentation.PREMIUM_PROSPECT;
        }
        
        return CustomerSegmentation.STANDARD_PROSPECT;
    }

    /**
     * Performs initial risk assessment.
     * This method can be hot-swapped to change risk assessment algorithms.
     * @return Initial risk profile
     */
    protected RiskProfile performInitialRiskAssessment() {
        // Basic risk assessment (can be hot-swapped)
        RiskLevel riskLevel = RiskLevel.MEDIUM;
        
        // Age-based risk (can be hot-swapped)
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 25) {
            riskLevel = RiskLevel.HIGH;
        } else if (age > 65) {
            riskLevel = RiskLevel.MEDIUM;
        } else {
            riskLevel = RiskLevel.LOW;
        }
        
        // Address-based risk (can be hot-swapped)
        if (primaryAddress != null && primaryAddress.isHighRiskArea()) {
            riskLevel = riskLevel.increase();
        }
        
        return new RiskProfile(riskLevel, "Initial assessment based on demographics");
    }

    /**
     * Performs initial compliance check.
     * This method can be hot-swapped to change compliance rules.
     * @return Initial compliance status
     */
    protected ComplianceStatus performInitialComplianceCheck() {
        // Basic compliance check (can be hot-swapped)
        List<String> requiredDocuments = new ArrayList<>();
        requiredDocuments.add("ID_VERIFICATION");
        requiredDocuments.add("ADDRESS_PROOF");
        
        // Additional requirements based on segmentation (can be hot-swapped)
        if (segmentation == CustomerSegmentation.PREMIUM_PROSPECT) {
            requiredDocuments.add("INCOME_VERIFICATION");
        }
        
        return new ComplianceStatus(false, requiredDocuments, "Pending document verification");
    }

    /**
     * Performs comprehensive risk assessment.
     * This method can be hot-swapped to change advanced risk algorithms.
     */
    protected void performComprehensiveRiskAssessment() {
        // Enhanced risk assessment (can be hot-swapped)
        RiskLevel currentLevel = riskProfile.getRiskLevel();
        String assessment = riskProfile.getAssessment();
        
        // Credit history check (simulated - can be hot-swapped)
        if (shouldPerformCreditCheck()) {
            Integer creditScore = performCreditCheck();
            this.creditScore = creditScore;
            
            if (creditScore != null && creditScore < 600) {
                currentLevel = currentLevel.increase();
                assessment += "; Low credit score detected";
            }
        }
        
        // Behavioral risk indicators (can be hot-swapped)
        if (hasRiskIndicators()) {
            currentLevel = currentLevel.increase();
            assessment += "; Risk indicators detected";
        }
        
        this.riskProfile = new RiskProfile(currentLevel, assessment);
        this.lastUpdatedAt = LocalDateTime.now();
        this.version++;
    }

    /**
     * Determines if credit check should be performed.
     * This method can be hot-swapped to change credit check criteria.
     * @return true if credit check required, false otherwise
     */
    protected boolean shouldPerformCreditCheck() {
        // Credit check criteria (can be hot-swapped)
        return segmentation == CustomerSegmentation.PREMIUM_PROSPECT ||
               segmentation == CustomerSegmentation.HIGH_VALUE ||
               riskProfile.getRiskLevel().ordinal() >= RiskLevel.MEDIUM.ordinal();
    }

    /**
     * Performs simulated credit check.
     * This method can be hot-swapped to integrate with different credit bureaus.
     * @return Credit score or null if unavailable
     */
    protected Integer performCreditCheck() {
        // Simulated credit check (can be hot-swapped)
        // In real implementation, this would call external credit bureau APIs
        
        // Simple simulation based on customer data
        int baseScore = 650;
        
        // Age factor (can be hot-swapped)
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age > 30) {
            baseScore += 50;
        }
        
        // Address factor (can be hot-swapped)
        if (primaryAddress != null && primaryAddress.isHighValueArea()) {
            baseScore += 100;
        }
        
        return Math.min(850, Math.max(300, baseScore));
    }

    /**
     * Checks for behavioral risk indicators.
     * This method can be hot-swapped to change risk detection algorithms.
     * @return true if risk indicators found, false otherwise
     */
    protected boolean hasRiskIndicators() {
        // Risk indicator detection (can be hot-swapped)
        
        // Email domain risk (can be hot-swapped)
        if (email.endsWith("@tempmail.com") || email.endsWith("@10minutemail.com")) {
            return true;
        }
        
        // Phone number validation (can be hot-swapped)
        if (phoneNumber != null && !phoneNumber.matches("^\\+?[1-9]\\d{1,14}$")) {
            return true;
        }
        
        return false;
    }

    /**
     * Checks if customer meets compliance requirements.
     * This method can be hot-swapped to change compliance rules.
     * @return true if compliant, false otherwise
     */
    protected boolean meetsComplianceRequirements() {
        // Compliance validation (can be hot-swapped)
        
        // Basic data completeness (can be hot-swapped)
        if (firstName == null || lastName == null || email == null || dateOfBirth == null) {
            return false;
        }
        
        // Age compliance (can be hot-swapped)
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age < 18) {
            return false; // GDPR and similar regulations
        }
        
        // Geographic compliance (can be hot-swapped)
        if (primaryAddress != null && primaryAddress.isRestrictedJurisdiction()) {
            return false;
        }
        
        // Risk-based compliance (can be hot-swapped)
        if (riskProfile.getRiskLevel() == RiskLevel.VERY_HIGH) {
            return false; // Enhanced due diligence required
        }
        
        return true;
    }

    /**
     * Finalizes customer segmentation based on comprehensive assessment.
     * This method can be hot-swapped to change segmentation algorithms.
     */
    protected void finalizeSegmentation() {
        // Advanced segmentation (can be hot-swapped)
        CustomerSegmentation newSegmentation = segmentation;
        
        // Credit-based segmentation (can be hot-swapped)
        if (creditScore != null) {
            if (creditScore >= 750) {
                newSegmentation = CustomerSegmentation.PREMIUM;
            } else if (creditScore >= 650) {
                newSegmentation = CustomerSegmentation.STANDARD;
            } else {
                newSegmentation = CustomerSegmentation.SUBPRIME;
            }
        }
        
        // Risk-based adjustment (can be hot-swapped)
        if (riskProfile.getRiskLevel().ordinal() >= RiskLevel.HIGH.ordinal()) {
            newSegmentation = newSegmentation.downgrade();
        }
        
        // Address-based premium upgrade (can be hot-swapped)
        if (primaryAddress != null && primaryAddress.isHighValueArea() && 
            newSegmentation != CustomerSegmentation.SUBPRIME) {
            newSegmentation = newSegmentation.upgrade();
        }
        
        this.segmentation = newSegmentation;
        this.lastUpdatedAt = LocalDateTime.now();
        this.version++;
    }

    /**
     * Calculates initial lifetime value estimate.
     * This method can be hot-swapped to change CLV algorithms.
     */
    protected void calculateInitialLifetimeValue() {
        // Lifetime value calculation (can be hot-swapped)
        BigDecimal baseValue = BigDecimal.valueOf(1000); // Base CLV
        
        // Segmentation multiplier (can be hot-swapped)
        switch (segmentation) {
            case PREMIUM:
                baseValue = baseValue.multiply(BigDecimal.valueOf(5.0));
                break;
            case HIGH_VALUE:
                baseValue = baseValue.multiply(BigDecimal.valueOf(3.0));
                break;
            case STANDARD:
                baseValue = baseValue.multiply(BigDecimal.valueOf(1.5));
                break;
            case SUBPRIME:
                baseValue = baseValue.multiply(BigDecimal.valueOf(0.5));
                break;
            default:
                // Keep base value
                break;
        }
        
        // Risk adjustment (can be hot-swapped)
        switch (riskProfile.getRiskLevel()) {
            case LOW:
                baseValue = baseValue.multiply(BigDecimal.valueOf(1.2));
                break;
            case MEDIUM:
                // No adjustment
                break;
            case HIGH:
                baseValue = baseValue.multiply(BigDecimal.valueOf(0.8));
                break;
            case VERY_HIGH:
                baseValue = baseValue.multiply(BigDecimal.valueOf(0.5));
                break;
        }
        
        this.lifetimeValue = baseValue;
        this.lastUpdatedAt = LocalDateTime.now();
        this.version++;
    }

    /**
     * Determines if customer is eligible for auto-activation.
     * This method can be hot-swapped to change activation criteria.
     * @return true if eligible for auto-activation, false otherwise
     */
    protected boolean isEligibleForAutoActivation() {
        // Auto-activation criteria (can be hot-swapped)
        
        // Risk-based criteria (can be hot-swapped)
        if (riskProfile.getRiskLevel().ordinal() >= RiskLevel.HIGH.ordinal()) {
            return false;
        }
        
        // Segmentation-based criteria (can be hot-swapped)
        if (segmentation == CustomerSegmentation.SUBPRIME) {
            return false;
        }
        
        // Compliance-based criteria (can be hot-swapped)
        if (!complianceStatus.isCompliant()) {
            return false;
        }
        
        // Credit score criteria (can be hot-swapped)
        if (creditScore != null && creditScore < 600) {
            return false;
        }
        
        return true;
    }

    /**
     * Activates the customer.
     */
    public void activate() {
        this.status = CustomerStatus.ACTIVE;
        this.lastUpdatedAt = LocalDateTime.now();
        this.version++;
    }

    /**
     * Updates customer segmentation based on new data.
     * This method can be hot-swapped to change segmentation logic.
     * @param newCreditScore Updated credit score
     * @param transactionHistory Transaction data
     */
    public void updateSegmentation(final Integer newCreditScore, final TransactionHistory transactionHistory) {
        this.creditScore = newCreditScore;
        
        // Re-evaluate segmentation (can be hot-swapped)
        CustomerSegmentation updatedSegmentation = calculateSegmentationFromData(
            newCreditScore, transactionHistory, riskProfile);
        
        if (updatedSegmentation != this.segmentation) {
            this.segmentation = updatedSegmentation;
            recalculateLifetimeValue(transactionHistory);
        }
        
        this.lastUpdatedAt = LocalDateTime.now();
        this.version++;
    }

    /**
     * Calculates segmentation from comprehensive data.
     * This method can be hot-swapped to change segmentation algorithms.
     * @param creditScore Credit score
     * @param transactionHistory Transaction history
     * @param riskProfile Risk profile
     * @return Updated segmentation
     */
    protected CustomerSegmentation calculateSegmentationFromData(
            final Integer creditScore,
            final TransactionHistory transactionHistory,
            final RiskProfile riskProfile) {
        
        // Advanced segmentation logic (can be hot-swapped)
        CustomerSegmentation newSegmentation = CustomerSegmentation.STANDARD;
        
        // Credit-based classification (can be hot-swapped)
        if (creditScore != null) {
            if (creditScore >= 800) {
                newSegmentation = CustomerSegmentation.PREMIUM;
            } else if (creditScore >= 700) {
                newSegmentation = CustomerSegmentation.HIGH_VALUE;
            } else if (creditScore >= 600) {
                newSegmentation = CustomerSegmentation.STANDARD;
            } else {
                newSegmentation = CustomerSegmentation.SUBPRIME;
            }
        }
        
        // Transaction volume adjustment (can be hot-swapped)
        if (transactionHistory != null) {
            BigDecimal totalVolume = transactionHistory.getTotalVolume();
            if (totalVolume.compareTo(BigDecimal.valueOf(100000)) > 0) {
                newSegmentation = newSegmentation.upgrade();
            } else if (totalVolume.compareTo(BigDecimal.valueOf(1000)) < 0) {
                newSegmentation = newSegmentation.downgrade();
            }
        }
        
        return newSegmentation;
    }

    /**
     * Recalculates lifetime value with updated data.
     * This method can be hot-swapped to change CLV algorithms.
     * @param transactionHistory Transaction history data
     */
    protected void recalculateLifetimeValue(final TransactionHistory transactionHistory) {
        // Enhanced CLV calculation (can be hot-swapped)
        BigDecimal baseValue = BigDecimal.valueOf(1000);
        
        // Historical transaction factor (can be hot-swapped)
        if (transactionHistory != null) {
            BigDecimal avgTransactionValue = transactionHistory.getAverageTransactionValue();
            int transactionFrequency = transactionHistory.getTransactionFrequency();
            
            baseValue = avgTransactionValue.multiply(BigDecimal.valueOf(transactionFrequency * 12)); // Annual projection
        }
        
        // Apply segmentation and risk multipliers (can be hot-swapped)
        baseValue = applySegmentationMultiplier(baseValue);
        baseValue = applyRiskAdjustment(baseValue);
        
        this.lifetimeValue = baseValue;
    }

    /**
     * Applies segmentation multiplier to CLV.
     * This method can be hot-swapped to change segmentation impact.
     * @param baseValue Base CLV value
     * @return Adjusted value
     */
    protected BigDecimal applySegmentationMultiplier(final BigDecimal baseValue) {
        switch (segmentation) {
            case PREMIUM:
                return baseValue.multiply(BigDecimal.valueOf(3.0));
            case HIGH_VALUE:
                return baseValue.multiply(BigDecimal.valueOf(2.0));
            case STANDARD:
                return baseValue.multiply(BigDecimal.valueOf(1.0));
            case SUBPRIME:
                return baseValue.multiply(BigDecimal.valueOf(0.6));
            default:
                return baseValue;
        }
    }

    /**
     * Applies risk adjustment to CLV.
     * This method can be hot-swapped to change risk impact.
     * @param baseValue Base CLV value
     * @return Risk-adjusted value
     */
    protected BigDecimal applyRiskAdjustment(final BigDecimal baseValue) {
        switch (riskProfile.getRiskLevel()) {
            case LOW:
                return baseValue.multiply(BigDecimal.valueOf(1.1));
            case MEDIUM:
                return baseValue.multiply(BigDecimal.valueOf(1.0));
            case HIGH:
                return baseValue.multiply(BigDecimal.valueOf(0.8));
            case VERY_HIGH:
                return baseValue.multiply(BigDecimal.valueOf(0.5));
            default:
                return baseValue;
        }
    }

    // Getters
    public String getCustomerId() { return customerId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Address getPrimaryAddress() { return primaryAddress; }
    public CustomerStatus getStatus() { return status; }
    public CustomerSegmentation getSegmentation() { return segmentation; }
    public RiskProfile getRiskProfile() { return riskProfile; }
    public ComplianceStatus getComplianceStatus() { return complianceStatus; }
    public BigDecimal getLifetimeValue() { return lifetimeValue; }
    public Integer getCreditScore() { return creditScore; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public Long getVersion() { return version; }

    // Setters for mutable fields
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.lastUpdatedAt = LocalDateTime.now();
        this.version++;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s %s', email='%s', status=%s, segment=%s}",
                           customerId, firstName, lastName, email, status, segmentation);
    }
}