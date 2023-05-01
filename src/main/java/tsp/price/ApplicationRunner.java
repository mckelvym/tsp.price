package tsp.price;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

/**
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Component
public class ApplicationRunner implements CommandLineRunner, ExitCodeGenerator {
    /**
     * PicoCLI command to run
     *
     * @author mckelvym
     * @since Apr 24, 2023
     */
    private final ApplicationCommand command;
    /**
     * Auto-configured to inject PicocliSpringFactory
     *
     * @author mckelvym
     * @since Apr 24, 2023
     */
    private final IFactory factory;
    /**
     * Exit code of the application
     *
     * @author mckelvym
     * @since Apr 24, 2023
     */
    private int exitCode;

    /**
     * @param command
     * @param factory
     * @author mckelvym
     * @since Apr 24, 2023
     */
    public ApplicationRunner(final ApplicationCommand command,
                             final IFactory factory) {
        this.command = command;
        this.factory = factory;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void run(final String... args) throws Exception {
        exitCode = new CommandLine(command, factory).execute(args);
    }
}