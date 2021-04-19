package com.budgeteer.cli;

import picocli.CommandLine;

import java.io.PrintWriter;

public class ParameterExceptionHandler implements CommandLine.IParameterExceptionHandler {
    @Override
    public int handleParseException(CommandLine.ParameterException ex, String[] args) throws Exception {
        CommandLine cmd = ex.getCommandLine();
        PrintWriter err = cmd.getErr();

        err.println(cmd.getColorScheme().errorText("<Input Error> " + ex.getMessage())); // bold red
        CommandLine.UnmatchedArgumentException.printSuggestions(ex, err);
        err.print(cmd.getHelp().fullSynopsis());

        CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();
        err.printf("Try '%s --help' for more information.%n", spec.qualifiedName());

        return cmd.getExitCodeExceptionMapper() != null
                ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
                : spec.exitCodeOnInvalidInput();
    }
}
