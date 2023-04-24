package tsp.price;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.io.Files;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * PicoCLI command
 *
 * @author mckelvym
 * @since Mar 30, 2023
 */
@Component
@Command(name = "scholarshare", mixinStandardHelpOptions = true)
public class ApplicationCommand implements Callable<Integer>
{

	/**
	 * Class logger
	 *
	 * @author mckelvym
	 * @since Mar 30, 2023
	 */
	private static final Logger log = LoggerFactory
			.getLogger(ApplicationCommand.class);

	/**
	 * Suppress normal logging from httpclient and http headers traffic.
	 *
	 * @since Feb 16, 2016
	 */
	private static void disableNoisyLogging()
	{
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

	/**
	 * @since Nov 23, 2017
	 */
	@Autowired
	private ConfigurableApplicationContext	context;

	/**
	 * @since Mar 8, 2020
	 */
	@Option(
			names = { "-m", "--merge-file" },
			description = "Merge with past entries in this file.",
			paramLabel = "merge-file.csv")
	private String							mergeFile;

	/**
	 * @since Mar 8, 2020
	 */
	@Option(
			names = { "-o", "--out-file" },
			description = "Output to this file, possibly with merges included (if specified)",
			paramLabel = "out-file.csv")
	private String							outFile;

	@Override
	public Integer call()
	{
		log.info("merge-file=" + mergeFile);
		log.info("out-file=" + outFile);

		disableNoisyLogging();

		@SuppressWarnings("null")
		final Response response = context.getBean(ServiceClient.class)
				.get(context.getBean(Request.class));

		response.getObservations().forEach(o ->
		{
			final List<String> elements = new ArrayList<>();
			final String date = String.valueOf(o.getDate());
			elements.add(date);
			final List<Fund> ordered = new ArrayList<>(Fund.getOrdered());
			Collections.reverse(ordered);
			ordered.stream().map(o.getValue()::get)
					.map(v -> String.format("%.2f", v)).forEach(elements::add);

			System.out.println(
					"Date," + ordered.stream().map(Fund::getDescription)
							.collect(Collectors.joining(",")));
			System.out.println(Joiner.on(",").join(elements));

			if (outFile != null)
			{
				log.info("Write to " + outFile);
				try (BufferedWriter writer = Files.newWriter(new File(outFile),
						Charset.defaultCharset()))
				{
					writer.write(
							"Date," + ordered.stream().map(Fund::getDescription)
									.collect(Collectors.joining(",")));
					writer.newLine();
					writer.write(Joiner.on(",").join(elements));
					writer.newLine();

					if (mergeFile != null && new File(mergeFile).exists())
					{
						log.info("Read from: " + mergeFile);
						Files.readLines(new File(mergeFile),
								Charset.defaultCharset()).stream().skip(1)
								.forEach(line ->
								{
									if (line.startsWith(date))
									{
										return;
									}
									try
									{
										writer.write(line);
										writer.newLine();
									}
									catch (final FileNotFoundException e)
									{
										final String message = "Unable to locate file: %s"
												.formatted(mergeFile);
										log.error(message, e);
									}
									catch (final IOException e)
									{
										final String message = "Unable to read: %s"
												.formatted(mergeFile);
										log.error(message, e);
									}
								});
					}
				}
				catch (final FileNotFoundException e)
				{
					final String message = "Unable to locate file: %s"
							.formatted(outFile);
					log.error(message, e);
				}
				catch (final IOException e)
				{
					final String message = "Unable to write to: %s"
							.formatted(outFile);
					log.error(message, e);
				}
			}
		});

		return 0;
	}
}