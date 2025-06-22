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
 * Filename: CleanupResult.java
 *
 * Author: Claude Code
 *
 * Class name: CleanupResult
 *
 * Responsibilities:
 *   - Encapsulate results of cleanup operations for rollback snapshots
 *   - Track performance metrics and resource utilization during cleanup
 *   - Provide detailed information about cleaned resources
 *
 * Collaborators:
 *   - RollbackManager: Uses this class to report cleanup operation results
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Duration;

/**
 * Results of cleanup operations for rollback snapshots
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class CleanupResult {

    /**
     * Whether the cleanup was successful
     */
    private final boolean successful;

    /**
     * Number of snapshots that were cleaned up
     */
    private final int cleanedSnapshotCount;

    /**
     * Time taken to perform the cleanup
     */
    private final Duration cleanupDuration;

    /**
     * Creates a new cleanup result
     * @param successful whether cleanup was successful
     * @param cleanedSnapshotCount number of snapshots cleaned
     * @param cleanupDuration time taken for cleanup
     */
    private CleanupResult(final boolean successful, final int cleanedSnapshotCount, final Duration cleanupDuration) {
        this.successful = successful;
        this.cleanedSnapshotCount = cleanedSnapshotCount;
        this.cleanupDuration = cleanupDuration;
    }

    /**
     * Creates a cleanup result
     * @param successful whether cleanup was successful
     * @param cleanedSnapshotCount number of snapshots cleaned
     * @param cleanupDuration time taken for cleanup
     * @return cleanup result
     */
    public static CleanupResult create(final boolean successful, final int cleanedSnapshotCount, final Duration cleanupDuration) {
        return new CleanupResult(successful, cleanedSnapshotCount, cleanupDuration);
    }

    /**
     * Returns whether the cleanup was successful
     * @return true if successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return "CleanupResult{" +
               "successful=" + successful +
               ", cleanedSnapshots=" + cleanedSnapshotCount +
               ", duration=" + cleanupDuration.toMillis() + "ms" +
               '}';
    }
}