package tsp.price;

import java.util.List;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tsp.price.config.OutputConfig;
import tsp.price.config.Properties;
import tsp.price.data.Observation;
import tsp.price.data.Observations;
import tsp.price.input.MergeFileReader;
import tsp.price.output.CsvWriter;
import tsp.price.output.RssWriter;
import tsp.price.output.StdOutWriter;
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
     * @since Dec 12, 2023
     */
    @Option(
            names = {"-r", "--out-rss-file"},
            description = "Output RSS to this file, possibly with merges included (if specified)",
            paramLabel = "feed.xml")
    private String outRssFile;

    /**
     * Suppress normal logging from httpclient and http headers traffic.
     *
     * @since Apr 24, 2023
     */
    private static void disableNoisyLogging() {
        // HTTP traffic log will be really noisy without this...
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
        log.info("out-rss-file=" + outRssFile);

        disableNoisyLogging();

        final Request request = context.getBean(Request.class);
        final ServiceClient serviceClient = context.getBean(ServiceClient.class);
        Observations response = serviceClient.get(request);
        final Properties properties = context.getBean(Properties.class);

        List<Observation> mergeObservations = mergeFile != null ? new MergeFileReader().parse(mergeFile) : List.of();
        List<Observation> observations = response.getObservations();
        new CsvWriter(new OutputConfig(mergeObservations, outFile)).writeObservations(observations);
        new RssWriter(new OutputConfig(mergeObservations, outRssFile), properties).writeObservations(observations);
        new StdOutWriter().writeObservations(observations);

        return 0;
    }
}
