package soRec;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
public class Main {

	private static final Logger logger = LogManager.getLogger(Main.class);
	public static void main(String[] args) 
	{
		HelpFormatter formatter = new HelpFormatter();

		Options opts = new Options()
				.addOption(Option.builder("indexFolder").desc("The path to where Lucene index is stored. SOrec " + 
						"downloads preloaded lucene index if the folder index is empty or doesn't exist.(1GB)").hasArg()
						.argName("indexFolder").required().build()).
				addOption(Option.builder("queryFolder").desc("The path to where snippets of code are stored.").hasArg()
						.argName("queryFolder").required().build());
		

		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(opts, args);
			Runner runner = new Runner(cmd.getOptionValue("indexFolder"), cmd.getOptionValue("queryFolder"));
			runner.run();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("SOrec", opts);
		} catch (org.apache.commons.cli.ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("SOrec", opts);
		}
	}

}
