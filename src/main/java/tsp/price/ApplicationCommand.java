package tsp.price;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tsp.price.data.Fund;
import tsp.price.data.Observations;
import tsp.price.xfer.Request;
import tsp.price.xfer.ServiceClient;

/**
 * PicoCLI command
 *
 * @author mckelvym
 * @since Apr 24, 2023
 */
@Component
@Command(name = "tsp", mixinStandardHelpOptions = true)
public class ApplicationCommand implements Callable<Integer> {

    /**
     * Class logger
     *
     * @author mckelvym
     * @since Apr 24, 2023
     */
    private static final Logger log = LoggerFactory
            .getLogger(ApplicationCommand.class);
    /**
     * @since Apr 24, 2023
     */
    @Autowired
    private ConfigurableApplicationContext context;
    /**
     * @since Apr 24, 2023
     */
    @Option(
            names = {"-m", "--merge-file"},
            description = "Merge with past entries in this file.",
            paramLabel = "merge-file.csv")
    private String mergeFile;
    /**
     * @since Apr 24, 2023
     */
    @Option(
            names = {"-o", "--out-file"},
            description = "Output to this file, possibly with merges included (if specified)",
            paramLabel = "out-file.csv")
    private String outFile;

    /**
     * Suppress normal logging from httpclient and http headers traffic.
     *
     * @since Apr 24, 2023
     */
    private static void disableNoisyLogging() {
        /**
         * HTTP traffic log will be really noisy without this...
         */
        java.util.logging.Logger.getLogger("org.apache.http.wire")
                .setLevel(java.util.logging.Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers")
                .setLevel(java.util.logging.Level.FINEST);
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
                "true");
        System.setProperty(
                "org.apache.commons.logging.simplelog.log.httpclient.wire",
                "ERROR");
        System.setProperty(
                "org.apache.commons.logging.simplelog.log.org.apache.http",
                "ERROR");
        System.setProperty(
                "org.apache.commons.logging.simplelog.log.org.apache.http.headers",
                "ERROR");
    }

    @Override
    public Integer call() {
        log.info("merge-file=" + mergeFile);
        log.info("out-file=" + outFile);

        disableNoisyLogging();

        @SuppressWarnings("null") final Observations response = context.getBean(ServiceClient.class)
                .get(context.getBean(Request.class));

        response.getObservations().forEach(o ->
        {
            final List<String> elements = new ArrayList<>();
            final String date = String.valueOf(o.getDate());
            elements.add(date);
            final List<Fund> ordered = new ArrayList<>(Fund.getOrdered());
            ordered.stream().map(o.getValue()::get)
                    .map(v -> String.format("%.4f", v)).forEach(elements::add);

            String delimiter = ", ";
            System.out.println(
                    "Date" + delimiter + ordered.stream().map(Fund::getDescription)
                            .collect(Collectors.joining(delimiter)));
            System.out.println(Joiner.on(delimiter).join(elements));

            if (outFile != null) {
                log.info("Write to " + outFile);
                try (BufferedWriter writer = Files.newWriter(new File(outFile),
                        Charset.defaultCharset())) {
                    writer.write(
                            "Date" + delimiter + ordered.stream().map(Fund::getDescription)
                                    .collect(Collectors.joining(delimiter)));
                    writer.newLine();
                    writer.write(Joiner.on(delimiter).join(elements));
                    writer.newLine();

                    if (mergeFile != null && new File(mergeFile).exists()) {
                        log.info("Read from: " + mergeFile);
                        Files.readLines(new File(mergeFile),
                                        Charset.defaultCharset()).stream().skip(1)
                                .forEach(line ->
                                {
                                    if (line.startsWith(date)) {
                                        return;
                                    }
                                    try {
                                        writer.write(line);
                                        writer.newLine();
                                    } catch (final FileNotFoundException e) {
                                        final String message = "Unable to locate file: %s"
                                                .formatted(mergeFile);
                                        log.error(message, e);
                                    } catch (final IOException e) {
                                        final String message = "Unable to read: %s"
                                                .formatted(mergeFile);
                                        log.error(message, e);
                                    }
                                });
                    }
                } catch (final FileNotFoundException e) {
                    final String message = "Unable to locate file: %s"
                            .formatted(outFile);
                    log.error(message, e);
                } catch (final IOException e) {
                    final String message = "Unable to write to: %s"
                            .formatted(outFile);
                    log.error(message, e);
                }
            }
        });

        return 0;
    }
}
