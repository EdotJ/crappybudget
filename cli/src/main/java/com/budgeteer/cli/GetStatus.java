package com.budgeteer.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "status", description = "Checks if budget application is already running")
public class GetStatus extends BaseCommand implements Runnable {

    public GetStatus(boolean isVerbose, CommandLine commandLine) {
        super(isVerbose, commandLine);
    }

    @Override
    public void run() {
        ProcessHandle processHandle = super.getRunningProcess();
        commandLine.getOut().println(processHandle != null
                ? "Application is running on PID " + processHandle.pid()
                : "Application is not running");
    }
}
