package soRec.Mongo2Lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.bson.Document;

import soRec.Utility.MongoUtilities.MongoManager;
import soRec.Utils.Jdt;
import soRec.Utils.Properties;

public class Mongo2Lucene {
	private static final Logger logger = LogManager.getLogger(Mongo2Lucene.class);
	private static String host;
	private static int port;
	private static String dbname;
	private static String colla;
	private static String collq;
	private static String collf;

	public Mongo2Lucene(String host, int port, String dbname, String colla, String collq, String collf) {
		Mongo2Lucene.host = host;
		Mongo2Lucene.port = port;
		Mongo2Lucene.dbname = dbname;
		Mongo2Lucene.colla = colla;
		Mongo2Lucene.collq = collq;
		Mongo2Lucene.collf = collf;
	}

	public void createLucene(String indexFolder) throws IOException, ParseException {
		Properties prop = new Properties();
		Jdt jdtParser = new Jdt();
		MongoManager mm = new MongoManager(host, port, dbname, colla, collq, collf);
		Mongo2LuceneFunctions mlf = new Mongo2LuceneFunctions();
		HashMap<String, String> classFix = jdtParser.createLibFixList();

		/*
		 * lucene config
		 */
		String INDEX_DIRECTORY = indexFolder;
		Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

		/*
		 * lucene ranking config
		 */
		if (prop.isBm25() == false) {
			ClassicSimilarity CS = new ClassicSimilarity();
			iwc.setSimilarity(CS);
		}
		/*
		 * 
		 */

		IndexWriter iw = new IndexWriter(indexDir, iwc);

		long start = System.nanoTime();

		while (prop.getMongoSkipValue() <= prop.getLimit()) {
			ArrayList<Document> lista = new ArrayList<Document>();
			lista = mm.loadLimited(prop);
			for (Document elem : lista) {
				org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
				doc = mlf.addTokens(jdtParser, elem, classFix);
				doc = mlf.addTitle(doc, elem);
				doc = mlf.addAnswerDiscussion(doc, elem);
				doc = mlf.addQuestionDiscussion(doc, elem);
				doc = mlf.addCode(doc, elem);
				iw.addDocument(doc);
			}
		}

		iw.close();
		long elapsedTime = System.nanoTime() - start;
		long elapsedTimeSeconds = elapsedTime / 1000000000;
		logger.info("time elapsed " + elapsedTimeSeconds);
	}

}
