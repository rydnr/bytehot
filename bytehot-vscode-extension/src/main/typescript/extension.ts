import * as vscode from 'vscode';
import * as path from 'path';
import * as fs from 'fs';
import { spawn } from 'child_process';

/**
 * ByteHot VS Code Extension.
 * 
 * Provides lightweight integration for live coding in VS Code.
 * Features bundled agent JAR for reliable deployment.
 */

let liveModeTerminal: vscode.Terminal | undefined;

export function activate(context: vscode.ExtensionContext) {
    console.log('ByteHot extension is now active!');

    // Register the "Start Live Mode" command
    let startLiveModeCommand = vscode.commands.registerCommand('bytehot.startLiveMode', () => {
        startLiveMode(context);
    });

    // Register the "Stop Live Mode" command
    let stopLiveModeCommand = vscode.commands.registerCommand('bytehot.stopLiveMode', () => {
        stopLiveMode();
    });

    context.subscriptions.push(startLiveModeCommand);
    context.subscriptions.push(stopLiveModeCommand);
}

export function deactivate() {
    if (liveModeTerminal) {
        liveModeTerminal.dispose();
    }
}

/**
 * Starts ByteHot live mode for the current workspace.
 */
async function startLiveMode(context: vscode.ExtensionContext) {
    try {
        // Find agent JAR (bundled or fallback)
        const agentPath = await findAgentJar(context);
        if (!agentPath) {
            vscode.window.showErrorMessage('ByteHot agent JAR not found. Please ensure bytehot-application is built.');
            return;
        }

        // Analyze current project
        const projectConfig = await analyzeProject();
        if (!projectConfig.mainClass) {
            vscode.window.showErrorMessage('No main class found. Please ensure your project has a class with main method.');
            return;
        }

        // Create terminal for live mode
        if (liveModeTerminal) {
            liveModeTerminal.dispose();
        }

        liveModeTerminal = vscode.window.createTerminal({
            name: 'ByteHot Live Mode',
            cwd: vscode.workspace.workspaceFolders?.[0].uri.fsPath
        });

        // Build launch command
        const command = buildLaunchCommand(projectConfig, agentPath);
        
        vscode.window.showInformationMessage(`Starting live mode for ${projectConfig.mainClass}`);
        
        // Execute command in terminal
        liveModeTerminal.sendText(command);
        liveModeTerminal.show();

    } catch (error) {
        vscode.window.showErrorMessage(`Failed to start live mode: ${error}`);
    }
}

/**
 * Stops the current live mode session.
 */
function stopLiveMode() {
    if (liveModeTerminal) {
        liveModeTerminal.dispose();
        liveModeTerminal = undefined;
        vscode.window.showInformationMessage('Live mode stopped');
    }
}

/**
 * Finds the ByteHot agent JAR file.
 * First tries bundled agent, then falls back to development locations.
 */
async function findAgentJar(context: vscode.ExtensionContext): Promise<string | null> {
    // Strategy 1: Extract bundled agent from extension resources
    try {
        const bundledAgentPath = await extractBundledAgent(context);
        if (bundledAgentPath && fs.existsSync(bundledAgentPath)) {
            return bundledAgentPath;
        }
    } catch (error) {
        console.log('Failed to extract bundled agent:', error);
    }

    // Strategy 2: Local Maven repository (for development)
    const userHome = process.env.HOME || process.env.USERPROFILE;
    if (userHome) {
        const localRepoPath = path.join(userHome, '.m2', 'repository', 'org', 'acmsl', 
            'bytehot-application', 'latest-SNAPSHOT', 'bytehot-application-latest-SNAPSHOT-agent.jar');
        if (fs.existsSync(localRepoPath)) {
            return localRepoPath;
        }
    }

    // Strategy 3: Current workspace relative path (for development)
    const workspaceRoot = vscode.workspace.workspaceFolders?.[0].uri.fsPath;
    if (workspaceRoot) {
        const workspacePath = path.join(workspaceRoot, 'bytehot-application', 'target', 
            'bytehot-application-latest-SNAPSHOT-agent.jar');
        if (fs.existsSync(workspacePath)) {
            return workspacePath;
        }
    }

    return null;
}

/**
 * Extracts the bundled ByteHot agent JAR from extension resources to a temporary file.
 */
async function extractBundledAgent(context: vscode.ExtensionContext): Promise<string | null> {
    const agentResourcePath = path.join(context.extensionPath, 'resources', 'agents', 'bytehot-application-agent.jar');
    
    if (!fs.existsSync(agentResourcePath)) {
        return null;
    }

    // Create temporary file
    const tempDir = require('os').tmpdir();
    const tempFile = path.join(tempDir, `bytehot-agent-${Date.now()}.jar`);

    // Copy resource to temporary file
    await fs.promises.copyFile(agentResourcePath, tempFile);

    return tempFile;
}

/**
 * Analyzes the current project to detect main class and build configuration.
 */
async function analyzeProject(): Promise<ProjectConfiguration> {
    const workspaceRoot = vscode.workspace.workspaceFolders?.[0].uri.fsPath;
    if (!workspaceRoot) {
        throw new Error('No workspace folder found');
    }

    // Simple main class detection (could be enhanced)
    const mainClass = await detectMainClass(workspaceRoot);
    const classpath = await buildClasspath(workspaceRoot);

    return {
        mainClass,
        classpath,
        workspaceRoot
    };
}

/**
 * Detects the main class in the project.
 */
async function detectMainClass(workspaceRoot: string): Promise<string | null> {
    // Look for Maven pom.xml first
    const pomPath = path.join(workspaceRoot, 'pom.xml');
    if (fs.existsSync(pomPath)) {
        try {
            const pomContent = await fs.promises.readFile(pomPath, 'utf8');
            // Look for exec plugin configuration
            const execMatch = pomContent.match(/<mainClass>([^<]+)<\/mainClass>/);
            if (execMatch) {
                return execMatch[1];
            }
        } catch (error) {
            console.log('Error reading pom.xml:', error);
        }
    }

    // Look for Gradle build files
    const gradlePath = path.join(workspaceRoot, 'build.gradle');
    const gradleKtsPath = path.join(workspaceRoot, 'build.gradle.kts');
    
    if (fs.existsSync(gradlePath) || fs.existsSync(gradleKtsPath)) {
        const buildFile = fs.existsSync(gradleKtsPath) ? gradleKtsPath : gradlePath;
        try {
            const buildContent = await fs.promises.readFile(buildFile, 'utf8');
            // Look for application plugin mainClassName
            const mainClassMatch = buildContent.match(/mainClassName\s*=\s*["']([^"']+)["']/);
            if (mainClassMatch) {
                return mainClassMatch[1];
            }
        } catch (error) {
            console.log('Error reading build file:', error);
        }
    }

    // Fallback: scan for Java files with main method
    return await scanForMainClass(workspaceRoot);
}

/**
 * Scans source directories for classes with main method.
 */
async function scanForMainClass(workspaceRoot: string): Promise<string | null> {
    const sourceDirs = [
        path.join(workspaceRoot, 'src', 'main', 'java'),
        path.join(workspaceRoot, 'src'),
    ];

    for (const sourceDir of sourceDirs) {
        if (fs.existsSync(sourceDir)) {
            const mainClass = await findMainClassInDirectory(sourceDir, '');
            if (mainClass) {
                return mainClass;
            }
        }
    }

    return null;
}

/**
 * Recursively searches for main class in directory.
 */
async function findMainClassInDirectory(dir: string, packageName: string): Promise<string | null> {
    const entries = await fs.promises.readdir(dir, { withFileTypes: true });

    for (const entry of entries) {
        const fullPath = path.join(dir, entry.name);
        
        if (entry.isDirectory()) {
            const subPackage = packageName ? `${packageName}.${entry.name}` : entry.name;
            const result = await findMainClassInDirectory(fullPath, subPackage);
            if (result) {
                return result;
            }
        } else if (entry.isFile() && entry.name.endsWith('.java')) {
            try {
                const content = await fs.promises.readFile(fullPath, 'utf8');
                if (content.includes('public static void main(')) {
                    const className = entry.name.replace('.java', '');
                    return packageName ? `${packageName}.${className}` : className;
                }
            } catch (error) {
                // Ignore file read errors
            }
        }
    }

    return null;
}

/**
 * Builds the classpath for the project.
 */
async function buildClasspath(workspaceRoot: string): Promise<string> {
    const classpaths: string[] = [];

    // Add Maven target classes
    const mavenClasses = path.join(workspaceRoot, 'target', 'classes');
    if (fs.existsSync(mavenClasses)) {
        classpaths.push(mavenClasses);
    }

    // Add Gradle build classes
    const gradleClasses = path.join(workspaceRoot, 'build', 'classes', 'main');
    if (fs.existsSync(gradleClasses)) {
        classpaths.push(gradleClasses);
    }

    // Add current directory as fallback
    classpaths.push(workspaceRoot);

    return classpaths.join(path.delimiter);
}

/**
 * Builds the launch command for the application with ByteHot agent.
 */
function buildLaunchCommand(config: ProjectConfiguration, agentPath: string): string {
    const parts = [
        'java',
        `-javaagent:"${agentPath}"`,
        '-cp',
        `"${config.classpath}"`,
        config.mainClass!
    ];

    return parts.join(' ');
}

/**
 * Project configuration interface.
 */
interface ProjectConfiguration {
    mainClass: string | null;
    classpath: string;
    workspaceRoot: string;
}