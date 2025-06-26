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

*/
package org.acmsl.bytehot.infrastructure.filesystem;

import org.acmsl.commons.patterns.Application;
import org.acmsl.commons.patterns.DomainEvent;
import org.acmsl.commons.patterns.DomainResponseEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FileWatcherAdapter.
 * @author Claude Code
 * @since 2025-06-24
 */
public class FileWatcherAdapterTest {

    /**
     * A temporary folder
     */
    @TempDir
    Path tempDir;

    /**
     * The file watcher
     */
    private FileWatcherAdapter watcher;

    /**
     * A mock {@code Application}
     */
    private Application mockApplication;

    /**
     * Common logic before each test
     * @throws Exception if the logic fails unexpectedly
     */
    @BeforeEach
    void setUp() throws Exception {
        // Create a simple mock application that just returns empty response lists
        mockApplication = new Application() {
            @Override
            public List<? extends DomainResponseEvent<?>> accept(final DomainEvent event) {
                return List.of(); // Return empty list for testing
            }
        };
        watcher = new FileWatcherAdapter(mockApplication);
    }

    @Test
    @DisplayName("Should create file watcher with application")
    public void shouldCreateFileWatcherWithApplication() {
        // When/Then
        assertNotNull(watcher);
        assertTrue(watcher.isWatcherAvailable());
    }

    /**
     * Should start watching valid directory
     * @throws Exception if the test fails unexpectedly
     */
    @Test
    @DisplayName("Should start watching valid directory")
    public void shouldStartWatchingValidDirectory() throws Exception {
        // Given
        final List<String> patterns = List.of("*.class");

        // When
        final String watchId = watcher.startWatching(tempDir, patterns, false);

        // Then
        assertNotNull(watchId);
        assertTrue(watcher.isWatching(tempDir));
        assertTrue(watcher.getWatchedPaths().contains(tempDir));
    }

    /**
     * Should handle non-existent directory gracefully
     */
    @Test
    @DisplayName("Should handle non-existent directory gracefully")
    public void shouldHandleNonExistentDirectoryGracefully() {
        // Given
        final Path nonExistentPath = tempDir.resolve("non-existent");
        final List<String> patterns = List.of("*.class");

        // When/Then - Should throw exception for non-existent directory
        assertThrows(IllegalArgumentException.class, () -> {
            watcher.startWatching(nonExistentPath, patterns, false);
        });
    }

    /**
     * Should stop watching gracefully
     * @throws Exception if the test fails unexpectedly
     */
    @Test
    @DisplayName("Should stop watching gracefully")
    public void shouldStopWatchingGracefully() throws Exception {
        // Given
        final List<String> patterns = List.of("*.class");
        final String watchId = watcher.startWatching(tempDir, patterns, false);

        // When/Then
        assertDoesNotThrow(() -> watcher.stopWatching(watchId));
        assertFalse(watcher.isWatching(tempDir));
    }

    /**
     * Should handle multiple watch paths
     * @throws Exception if the test fails unexpectedly
     */
    @Test
    @DisplayName("Should handle multiple watch paths")
    public void shouldHandleMultipleWatchPaths() throws Exception {
        // Given
        final Path dir1 = Files.createDirectory(tempDir.resolve("dir1"));
        final Path dir2 = Files.createDirectory(tempDir.resolve("dir2"));
        final List<String> patterns = List.of("*.class");

        // When
        final String watchId1 = watcher.startWatching(dir1, patterns, false);
        final String watchId2 = watcher.startWatching(dir2, patterns, false);

        // Then
        assertNotNull(watchId1);
        assertNotNull(watchId2);
        assertTrue(watcher.isWatching(dir1));
        assertTrue(watcher.isWatching(dir2));
        assertEquals(2, watcher.getWatchedPaths().size());
    }

    /**
     * Should be able to restart watching
     * @throws Exception if the test fails unexpectedly
     */
    @Test
    @DisplayName("Should be able to restart watching")
    public void shouldBeAbleToRestartWatching() throws Exception {
        // Given
        final List<String> patterns = List.of("*.class");

        // When/Then
        assertDoesNotThrow(() -> {
            final String watchId1 = watcher.startWatching(tempDir, patterns, false);
            watcher.stopWatching(watchId1);
            final String watchId2 = watcher.startWatching(tempDir, patterns, false);
            watcher.stopWatching(watchId2);
        });
    }

    /**
     * Should  check if currently watching
     * @throws Exception if the test fails unexpectedly
     */
    @Test
    @DisplayName("Should check if currently watching")
    public void shouldCheckIfCurrentlyWatching() throws Exception {
        // Given
        final List<String> patterns = List.of("*.class");

        // When/Then
        assertFalse(watcher.isWatching(tempDir));
        final String watchId = watcher.startWatching(tempDir, patterns, false);
        assertTrue(watcher.isWatching(tempDir));
        watcher.stopWatching(watchId);
        assertFalse(watcher.isWatching(tempDir));
    }

    /**
     * Should support recursive watching
     * @throws Exception if the test fails unexpectedly
     */
    @Test
    @DisplayName("Should support recursive watching")
    public void shouldSupportRecursiveWatching() throws Exception {
        // Given
        final Path subDir = Files.createDirectory(tempDir.resolve("subdir"));
        final List<String> patterns = List.of("*.class");

        // When
        final String watchId = watcher.startWatching(tempDir, patterns, true);

        // Then
        assertNotNull(watchId);
        assertTrue(watcher.isWatching(tempDir));
        assertTrue(watcher.isWatching(subDir));
    }

    /**
     * Should be operational after initialization
     */
    @Test
    @DisplayName("Should be operational after initialization")
    public void shouldBeOperationalAfterInitialization() {
        // When/Then
        assertTrue(watcher.isWatcherAvailable());
    }
}
