package com.budgeteer.cli;

import picocli.CommandLine;

public class ExecutionExceptionHandler implements CommandLine.IExecutionExceptionHandler {
    @Override
    public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) throws Exception {
        // bold red error message
        cmd.getErr().println(cmd.getColorScheme().errorText("<Execution Error> " + ex.getMessage()));

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : cmd.getCommandSpec().exitCodeOnExecutionException();
    }
}
