package com.budgeteer.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.concurrent.Callable;

@Command(name = "budget-cli", mixinStandardHelpOptions = true,
        version = "budget-cli 1.0",
        description = "Wrapper for budget application",
        synopsisSubcommandLabel = "<command>")
public class Application extends BaseCommand implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"},
            defaultValue = "false",
            description = "Print verbose output")
    private static boolean isVerbose;

    @Override
    public Integer call() throws Exception {
        getCommandLine().usage(System.out);
        return CommandLine.ExitCode.USAGE;
    }

    public static void main(String... args) throws IOException, InterruptedException {
        CommandLine cmd = getCommandLine();
        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }

    public static CommandLine getCommandLine() {
        CommandLine commandLine = new CommandLine(new Application());
        return commandLine.addSubcommand(new StartBudget(isVerbose, commandLine))
                .addSubcommand(new GetStatus(isVerbose, commandLine))
                .addSubcommand(new StopBudget(isVerbose, commandLine))
                .setParameterExceptionHandler(new ParameterExceptionHandler())
                .setExecutionExceptionHandler(new ExecutionExceptionHandler());
    }
}