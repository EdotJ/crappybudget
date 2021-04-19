package com.budgeteer.cli;

import org.apache.commons.lang3.SystemUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "start", description = "Starts the budget application for specified path",
        synopsisSubcommandLabel = "[BUDGET_APP_JAR]")
public class StartBudget extends BaseCommand implements Runnable {

    @CommandLine.Parameters(defaultValue = "./budget.jar",
            description = "Path to application jar. Defaults to ${DEFAULT-VALUE}")
    private String path;

    public StartBudget(boolean isVerbose, CommandLine commandLine) {
        super(isVerbose, commandLine);
    }

    @Override
    public void run() {
        Path pathObj = Paths.get(path.replace("./", ""));
        if (!Files.exists(pathObj)) {
            throw new CommandLine.ParameterException(commandLine, "Budget Jar not found :(");
        }
        Path absPath = pathObj.toAbsolutePath();
        boolean isApplicationRunning = checkIfRunning();
        if (!isApplicationRunning) {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(getAppExecutionCommand(absPath).split(" "));
            try {
                Process process = builder.start();
                writeCommandLineToTemp(process);
            } catch (IOException e) {
                e.printStackTrace();
            }
            printVerboseMessage("Application started successfully");
        }
        printVerboseMessage("Application is already running");
    }

    private void writeCommandLineToTemp(Process process) {
        try {
            Path temp = Files.createTempFile("budget_app", ".tmp");
            byte[] bytes = process.info().commandLine().orElse("budget.jar").getBytes(Charset.defaultCharset());
            Files.write(temp, bytes);
            String absolutePath = temp.toString();
            printVerboseMessage("Temp process name file : " + absolutePath);

            String separator = FileSystems.getDefault().getSeparator();
            String tempFilePath = absolutePath
                    .substring(0, absolutePath.lastIndexOf(separator));

            printVerboseMessage("Temp process name file path : " + tempFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAppExecutionCommand(Path absPath) {
        StringBuilder sb = new StringBuilder();
        if (SystemUtils.IS_OS_WINDOWS) {
            sb.append("cmd.exe ");
        }
        sb.append("java -jar ");
        sb.append(absPath);
        sb.append(" &");
        printVerboseMessage("App execution command: " + sb);
        return sb.toString();
    }
}
