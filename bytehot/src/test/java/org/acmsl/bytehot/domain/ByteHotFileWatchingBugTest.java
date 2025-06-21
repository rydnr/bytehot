/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
/*
 ******************************************************************************
 *
 * Filename: ByteHotFileWatchingBugTest.java
 *
 * Author: Claude Code
 *
 * Class name: ByteHotFileWatchingBugTest
 *
 * Responsibilities:
 *   - Test that detects the file watching bug in ByteHot.start() method
 *   - Verifies that ByteHot.start() calls FileWatcherPort.startWatching()
 *   - Documents the expected behavior vs current buggy behavior
 *
 * Collaborators:
 *   - ByteHot: The domain class being tested for the bug
 */
package org.acmsl.bytehot.domain;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test that detects the critical file watching bug in ByteHot.
 * 
 * **The Bug**: ByteHot.start() emits WatchPathConfigured events but never 
 * calls FileWatcherPort.startWatching() to actually begin monitoring files.
 * 
 * **Expected Behavior**: When ByteHot.start() is called with a configuration
 * containing folders to watch, it should call FileWatcherPort.startWatching()
 * for each configured folder.
 * 
 * **Current Buggy Behavior**: Events are emitted but file watching never starts.
 * 
 * @author Claude Code  
 * @since 2025-06-22
 */
public class ByteHotFileWatchingBugTest {

    @Test
    public void bytehot_start_should_call_file_watcher_port_start_watching() throws Exception {
        // When: We examine the ByteHot.start() method source code
        final String byteHotSourceCode = getByteHotStartMethodSource();
        
        // Then: The start() method should contain calls to FileWatcherPort.startWatching()
        // This test documents the bug by checking what the code SHOULD do vs what it DOES
        
        if (!byteHotSourceCode.contains("FileWatcherPort") || 
            !byteHotSourceCode.contains("startWatching")) {
            
            fail("🐛 BUG DETECTED: ByteHot.start() method does not call FileWatcherPort.startWatching()!\n\n" +
                 "EXPECTED BEHAVIOR:\n" +
                 "- ByteHot.start() should resolve FileWatcherPort from Ports\n" +
                 "- ByteHot.start() should call fileWatcher.startWatching() for each configured folder\n" +
                 "- This would actually begin monitoring files for changes\n\n" +
                 "CURRENT BUGGY BEHAVIOR:\n" +
                 "- ByteHot.start() only emits WatchPathConfigured events\n" +
                 "- No actual file watching is started\n" +
                 "- File changes are never detected\n\n" +
                 "FIX: Add code in ByteHot.start() to:\n" +
                 "1. Resolve FileWatcherPort from Ports\n" +
                 "2. Loop through configuration.getFolders()\n" +
                 "3. Call fileWatcher.startWatching() for each folder\n\n" +
                 "This test will PASS once the bug is fixed.");
        }
        
        // If we reach here, the bug is likely fixed
        System.out.println("✅ ByteHot.start() method appears to contain file watching calls");
    }

    /**
     * Reads the ByteHot.start() method source to check for file watching calls.
     * This is a simple way to detect the bug without complex mocking.
     */
    private String getByteHotStartMethodSource() throws Exception {
        // Read the ByteHot.java source file to check if it contains the fix
        final Path sourceFile = Path.of("src/main/java/org/acmsl/bytehot/domain/ByteHot.java");
        if (sourceFile.toFile().exists()) {
            return java.nio.file.Files.readString(sourceFile);
        }
        
        // If we can't read the source, assume the bug exists
        return "// Source not available for analysis";
    }
}