import com.budgeteer.cli.Application;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTest {
    CommandLine cmd = Application.getCommandLine();

    @Test
    public void testApplicationFlow() {
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));
        int exitCode = cmd.execute("--help");
        assertEquals(0, exitCode);
        exitCode = cmd.execute("start", "/home/budget123456789.jar");
        assertEquals(2, exitCode);
        exitCode = cmd.execute("status");
        assertEquals(0, exitCode);
        String output = sw.toString();
        assertTrue(output.contains("Application is not running"));
        exitCode = cmd.execute("stop");
        assertEquals(0, exitCode);
        System.out.println(output);
    }
}
