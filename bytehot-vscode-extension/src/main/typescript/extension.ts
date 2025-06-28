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
let statusBarItem: vscode.StatusBarItem;
let outputChannel: vscode.OutputChannel;
let isLiveModeActive = false;

export function activate(context: vscode.ExtensionContext) {
    console.log('ByteHot extension is now active!');

    // Initialize output channel
    outputChannel = vscode.window.createOutputChannel('ByteHot');
    outputChannel.appendLine('ByteHot extension activated');
    
    // Initialize status bar item
    statusBarItem = vscode.window.createStatusBarItem(vscode.StatusBarAlignment.Left, 100);
    statusBarItem.command = 'bytehot.toggleLiveMode';
    updateStatusBar();
    statusBarItem.show();

    // Register commands
    let startLiveModeCommand = vscode.commands.registerCommand('bytehot.startLiveMode', () => {
        startLiveMode(context);
    });

    let stopLiveModeCommand = vscode.commands.registerCommand('bytehot.stopLiveMode', () => {
        stopLiveMode();
    });
    
    let toggleLiveModeCommand = vscode.commands.registerCommand('bytehot.toggleLiveMode', () => {
        if (isLiveModeActive) {
            stopLiveMode();
        } else {
            startLiveMode(context);
        }
    });

    let showOutputCommand = vscode.commands.registerCommand('bytehot.showOutput', () => {
        outputChannel.show();
    });

    // Register all commands with context
    context.subscriptions.push(
        startLiveModeCommand,
        stopLiveModeCommand,
        toggleLiveModeCommand,
        showOutputCommand,
        statusBarItem,
        outputChannel
    );
}

export function deactivate() {
    if (liveModeTerminal) {
        liveModeTerminal.dispose();
    }
    isLiveModeActive = false;
}

/**
 * Updates the status bar item based on current live mode state.
 */
function updateStatusBar() {
    if (isLiveModeActive) {
        statusBarItem.text = '$(debug-stop) ByteHot: Active';
        statusBarItem.tooltip = 'ByteHot live mode is running. Click to stop.';
        statusBarItem.backgroundColor = new vscode.ThemeColor('statusBarItem.prominentBackground');
    } else {
        statusBarItem.text = '$(play) ByteHot: Ready';
        statusBarItem.tooltip = 'ByteHot live mode is ready. Click to start.';
        statusBarItem.backgroundColor = undefined;
    }
}

/**
 * Starts ByteHot live mode for the current workspace.
 */
async function startLiveMode(context: vscode.ExtensionContext) {
    if (isLiveModeActive) {
        vscode.window.showWarningMessage('ByteHot live mode is already running');
        return;
    }

    try {
        outputChannel.appendLine('Starting ByteHot live mode...');
        
        // Find agent JAR (bundled or fallback)
        const agentPath = await findAgentJar(context);
        if (!agentPath) {
            const message = 'ByteHot agent JAR not found. Please ensure bytehot-application is built.';
            outputChannel.appendLine(`ERROR: ${message}`);
            vscode.window.showErrorMessage(message);
            return;
        }
        
        outputChannel.appendLine(`Found agent JAR: ${agentPath}`);

        // Analyze current project
        const projectConfig = await analyzeProject();
        if (!projectConfig.mainClass) {
            const message = 'No main class found. Please ensure your project has a class with main method.';
            outputChannel.appendLine(`ERROR: ${message}`);
            vscode.window.showErrorMessage(message);
            return;
        }
        
        outputChannel.appendLine(`Detected main class: ${projectConfig.mainClass}`);
        outputChannel.appendLine(`Classpath: ${projectConfig.classpath}`);

        // Apply user configuration overrides
        const config = vscode.workspace.getConfiguration('bytehot');
        const userMainClass = config.get<string>('mainClass');
        const userJvmArgs = config.get<string[]>('jvmArgs') || [];
        
        if (userMainClass && userMainClass.trim()) {
            projectConfig.mainClass = userMainClass.trim();
            outputChannel.appendLine(`Using configured main class: ${projectConfig.mainClass}`);
        }

        // Create terminal for live mode
        if (liveModeTerminal) {
            liveModeTerminal.dispose();
        }

        liveModeTerminal = vscode.window.createTerminal({
            name: 'ByteHot Live Mode',
            cwd: vscode.workspace.workspaceFolders?.[0].uri.fsPath
        });

        // Build launch command with user JVM args
        const command = buildLaunchCommand(projectConfig, agentPath, userJvmArgs);
        
        outputChannel.appendLine(`Launch command: ${command}`);
        
        const message = `Starting live mode for ${projectConfig.mainClass}`;
        vscode.window.showInformationMessage(message);
        outputChannel.appendLine(message);
        
        // Execute command in terminal
        liveModeTerminal.sendText(command);
        liveModeTerminal.show();
        
        // Update state
        isLiveModeActive = true;
        updateStatusBar();
        
        // Monitor terminal for closure
        vscode.window.onDidCloseTerminal((terminal) => {
            if (terminal === liveModeTerminal) {
                handleLiveModeTerminated();
            }
        });

    } catch (error) {
        const message = `Failed to start live mode: ${error}`;
        outputChannel.appendLine(`ERROR: ${message}`);
        vscode.window.showErrorMessage(message);
    }
}

/**
 * Stops the current live mode session.
 */
function stopLiveMode() {
    if (!isLiveModeActive) {
        vscode.window.showWarningMessage('ByteHot live mode is not running');
        return;
    }

    outputChannel.appendLine('Stopping ByteHot live mode...');
    
    if (liveModeTerminal) {
        liveModeTerminal.dispose();
        liveModeTerminal = undefined;
    }
    
    isLiveModeActive = false;
    updateStatusBar();
    
    const message = 'ByteHot live mode stopped';
    vscode.window.showInformationMessage(message);
    outputChannel.appendLine(message);
}

/**
 * Handles live mode terminal termination.
 */
function handleLiveModeTerminated() {
    if (isLiveModeActive) {
        outputChannel.appendLine('Live mode terminal was closed');
        isLiveModeActive = false;
        updateStatusBar();
        liveModeTerminal = undefined;
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
function buildLaunchCommand(config: ProjectConfiguration, agentPath: string, jvmArgs: string[] = []): string {
    const parts = [
        'java',
        `-javaagent:"${agentPath}"`
    ];
    
    // Add additional JVM arguments
    if (jvmArgs.length > 0) {
        parts.push(...jvmArgs);
    }
    
    parts.push(
        '-cp',
        `"${config.classpath}"`,
        config.mainClass!
    );

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