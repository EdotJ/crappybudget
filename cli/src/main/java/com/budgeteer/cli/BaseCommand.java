package com.budgeteer.cli;

import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BaseCommand {

    private boolean isVerbose;

    protected CommandLine commandLine;

    public BaseCommand() {

    }

    public BaseCommand(boolean isVerbose, CommandLine commandLine) {
        this.isVerbose = isVerbose;
        this.commandLine = commandLine;
    }

    protected ProcessHandle getRunningProcess() {
        String commandLine = getCommandLineFromTemp();
        if (commandLine == null) {
            return null;
        }
        List<ProcessHandle> processHandleList = ProcessHandle.allProcesses()
                .filter(ph -> isBudgetProcess(ph, commandLine))
                .collect(Collectors.toList());
        return processHandleList.size() > 0 ? processHandleList.get(0) : null;
    }

    protected boolean checkIfRunning() {
        return getRunningProcess() != null;
    }

    protected boolean isBudgetProcess(ProcessHandle ph, String commandLine) {
        // todo: what if command changes?
        return ph.info().commandLine().isPresent()
                && ph.info().commandLine().get().equals(commandLine)
                && ph.isAlive();
    }

    private String getCommandLineFromTemp() {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(getTempDirPath(), Files::isRegularFile);
            List<Path> paths = StreamSupport.stream(stream.spliterator(), false)
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().startsWith("budget_app"))
                    .collect(Collectors.toList());
            stream.close();
            if (paths.size() > 0) {
                return new String(Files.readAllBytes(paths.get(0)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Path getTempDirPath() {
        String tempFolderPath = System.getProperty("java.io.tmpdir");
        return Path.of(tempFolderPath);
    }

    protected void printVerboseMessage(String message) {
        if (isVerbose) {
            System.out.println(message);
        }
    }
}
