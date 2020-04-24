package soRec.Dump2Mongo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

import soRec.Mongo2Lucene.Mongo2Lucene;
import soRec.Utility.MongoUtilities.MongoManager;

public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class);
	private static final String collA = "postsAnew";
	private static final String collQ = "postsQnew";
	private static final String collF = "postsFnew";
	private static final String REMOTE_URL = "https://onedrive.live.com/download?resid=A33324427A144A54!1980&authkey=!AKZ37LwWqQReNtQ";
	
	public static void main(String[] args) {
		HelpFormatter formatter = new HelpFormatter();
		Options opts = new Options()
				.addOption(Option.builder("postdump")
						.desc("The path to where SO dump index is stored. SOrec_Recommender "
								+ "downloads preloaded so dumps (700MB) if the file index "
								+ "doesn't exist.")
						.hasArg().argName("postdump").required().build())
				.addOption(Option.builder("indexFolder").desc("The path to where snippets of code are stored.").hasArg()
						.argName("indexFolder").required().build())
				.addOption(Option.builder("dbport").desc("Monmgodb port (27017)").hasArg().argName("dbport").build())
				.addOption(Option.builder("dbname").desc("Monmgodb port (stackof)").hasArg().argName("dbname").build())
				.addOption(
						Option.builder("dbhost").desc("Monmgodb host (localhost)").hasArg().argName("dbhost").build());

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(opts, args);
			String inputFile = cmd.getOptionValue("postdump");
			String host = cmd.getOptionValue("dbhost", "localhost");
			String dbname = cmd.getOptionValue("dbname", "stackof");
			int port = Integer.parseInt(cmd.getOptionValue("dbport", "27017"));
			String indexFolder = cmd.getOptionValue("indexFolder", "indexFolder");
			logger.info("populating mongo collections");
			loadXMLtoMongo(host, port, dbname, inputFile);
			logger.info("populated mong collection");
			logger.info("Creating lucene index");
			Mongo2Lucene m2l = new Mongo2Lucene(host, port, dbname, collA, collQ, collF);
			logger.info("Created luce index");
			m2l.createLucene(indexFolder);
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (ParseException e) {
			logger.error(e.getMessage());
		} catch (org.apache.commons.cli.ParseException e) {
			logger.error(e.getMessage());
			formatter.printHelp("SORec - Indexer", opts);
		} catch (SAXException e) {
			logger.error(e.getMessage());
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
		}

	}

	private static void loadXMLtoMongo(String host, int port, String dbname, String inputFile) throws SAXException, IOException, ParserConfigurationException {
		retrieveIndex(inputFile);
		UserHandler userhandler = new UserHandler(host, port, dbname, collQ, collA);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(inputFile, userhandler);
		MongoManager mm = new MongoManager(host, port, dbname, collA, collQ, collF);
		mm.search();
	}
	
	private static void retrieveIndex(String inputFile) {
		if (Files.exists(Paths.get(inputFile))) {
			logger.info("Index archive found. Skipping download.");
			return;
		} else {
			logger.warn("Couldn't find the Lucene Indexes. I will download and extract it for you (~700MB).");
			logger.warn("Downloading Post.xml from {} to {}", REMOTE_URL, inputFile);
			try {
				FileUtils.copyURLToFile(new URL(REMOTE_URL), new File(inputFile), 5000, 5000);
			} catch (IOException e) {
				logger.error("Couldn't download index archive", e);
			}
		}
	}
	
}
