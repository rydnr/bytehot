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
 * Filename: CascadingRollbackResult.java
 *
 * Author: Claude Code
 *
 * Class name: CascadingRollbackResult
 *
 * Responsibilities:
 *   - Encapsulate results of cascading rollback operations across multiple classes
 *   - Aggregate individual rollback results and provide overall success status
 *   - Support analysis of partial failures in cascading operations
 *
 * Collaborators:
 *   - RollbackResult: Individual rollback results that are aggregated
 *   - RollbackManager: Creates cascading results for multi-class rollbacks
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.util.List;

/**
 * Results of cascading rollback operations across multiple classes
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class CascadingRollbackResult {

    /**
     * Individual rollback results for each class
     */
    private final List<RollbackResult> rollbackResults;

    /**
     * Whether the overall cascading operation was successful
     */
    private final boolean overallSuccessful;

    /**
     * Creates a new cascading rollback result
     * @param rollbackResults individual rollback results
     * @param overallSuccessful overall success status
     */
    private CascadingRollbackResult(final List<RollbackResult> rollbackResults, final boolean overallSuccessful) {
        this.rollbackResults = rollbackResults;
        this.overallSuccessful = overallSuccessful;
    }

    /**
     * Creates a cascading rollback result
     * @param rollbackResults individual rollback results
     * @param overallSuccessful overall success status
     * @return cascading rollback result
     */
    public static CascadingRollbackResult create(final List<RollbackResult> rollbackResults, final boolean overallSuccessful) {
        return new CascadingRollbackResult(rollbackResults, overallSuccessful);
    }

    /**
     * Gets the number of successful individual rollbacks
     * @return count of successful rollbacks
     */
    public long getSuccessfulCount() {
        return rollbackResults.stream().mapToLong(result -> result.isSuccessful() ? 1 : 0).sum();
    }

    /**
     * Gets the number of failed individual rollbacks
     * @return count of failed rollbacks
     */
    public long getFailedCount() {
        return rollbackResults.stream().mapToLong(result -> result.isSuccessful() ? 0 : 1).sum();
    }

    /**
     * Gets the total number of rollback operations
     * @return total count
     */
    public int getTotalCount() {
        return rollbackResults.size();
    }

    /**
     * Returns whether all individual rollbacks were successful
     * @return true if all succeeded
     */
    public boolean isOverallSuccessful() {
        return overallSuccessful;
    }

    @Override
    public String toString() {
        return "CascadingRollbackResult{" +
               "totalOperations=" + getTotalCount() +
               ", successful=" + getSuccessfulCount() +
               ", failed=" + getFailedCount() +
               ", overallSuccessful=" + overallSuccessful +
               '}';
    }
}