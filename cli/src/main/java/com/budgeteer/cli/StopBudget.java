package com.budgeteer.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "stop", description = "Stops the budget application for previously started process")
public class StopBudget extends BaseCommand implements Runnable {

    public StopBudget(boolean isVerbose, CommandLine commandLine) {
        super(isVerbose, commandLine);
    }

    @Override
    public void run() {
        ProcessHandle processHandle = getRunningProcess();
        if (processHandle != null) {
            processHandle.destroy();
            System.out.println("Stopped application");
        } else {
            printVerboseMessage("Application is not running");
        }
    }
}
