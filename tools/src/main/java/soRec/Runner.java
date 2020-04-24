package soRec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import soRec.LuceneQuery.QueryManager;
import soRec.Utils.CodeCleaner;
import soRec.Utils.Entropy;
import soRec.Utils.ImportFinder;
import soRec.Utils.Jdt;
import soRec.Utils.Properties;
import soRec.Utils.ResultsModel;
import soRec.Utils.SimianIntegration;
import soRec.Utils.SnippetReader;

public class Runner {
	private static final String REMOTE_INDEX = "https://onedrive.live.com/download?resid=A33324427A144A54!1978&authkey=!ALQJVzTrtKlrmZI";
	private static final Logger logger = LogManager.getLogger(Runner.class);
	Entropy entropy = new Entropy();
	Properties prop = new Properties();
	QueryManager qm = new QueryManager();
	SnippetReader sr = new SnippetReader();
	final File folder;
	ImmutableSet<String> config = prop.getConfig();
	final String INDEX_DIRECTORY;
	Jdt jdtParser = new Jdt();
	CodeCleaner cc = new CodeCleaner();
	SimianIntegration simian = new SimianIntegration();

	public Runner(String indexPath, String contextPath) {
		folder = new File(contextPath);
		INDEX_DIRECTORY = indexPath;
	}

	public void run() throws IOException, ParseException {
		retrieveIndex();
		logger.info("Start");

		List<ResultsModel> resList = Lists.newArrayList();
		for (final File fileEntry : folder.listFiles()) {
			resList.add(execute(fileEntry));
		}

		for (ResultsModel res : resList) {
			logger.info("###############");
			logger.info(res.getFileName());
			logger.info(res.getQuery());
			for (int i = 0; i < prop.getLuceneTreshold(); i++) {
				try {
					logger.info("\thttps://stackoverflow.com/questions/" + res.getIDsPost().get(i));
				} catch (Exception exc) {
					logger.error(res.getFileName() + " Failure");
				}
			}

		}
	}

	public ResultsModel execute(File fileEntry) throws IOException, ParseException {
		ResultsModel res = new ResultsModel();
		ImportFinder iF = new ImportFinder();
		Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		IndexReader reader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(reader);
		String snippet = "";
		/*
		 * snippet loading
		 */
		snippet = sr.readFromFile(fileEntry);
		/*
		 * parsing
		 */
		ArrayList<HashMap<String, String>> tokens = jdtParser.run(snippet, false, null, null);
		tokens = jdtParser.cleanDuplicates(tokens);
		/*
		 * entropy calculation
		 */
		HashMap<String, Double> entropies = entropy(snippet, tokens);
		/*
		 * fix variable decl type
		 */
		ArrayList<String> imports = cc.varDeclTypeFix(tokens);
		/*
		 * query writing
		 */
		String query = qm.CreateBoostedQuery(config, tokens, imports, entropies, iF.createLocalImportList(), prop);
		query = cc.squareRemover(query);
		res.setQuery(query);
		/*
		 * query execution
		 */
		String[] args = null;
		TopDocs results = qm.query(args, INDEX_DIRECTORY, query, prop);
		if (results != null) {
			res.setFileName(fileEntry.getName());
			res.setTotalHits(results.totalHits);
			int counter = 0;
			ArrayList<Explanation> expls = new ArrayList<Explanation>();
			ArrayList<Float> scores = new ArrayList<Float>();
			ArrayList<String> Ids = new ArrayList<String>();
			ArrayList<Integer> simians = new ArrayList<Integer>();
			Set<String> resAux = new HashSet<String>();
			for (ScoreDoc result : results.scoreDocs) {
				if (counter < prop.getLuceneTreshold()) {
					org.apache.lucene.document.Document d = searcher.doc(result.doc);
					if (resAux.contains(d.get("ID_POST"))) {
						continue;
					} else {
						expls.add(searcher.explain(prop.getQ(), result.doc));
						scores.add(result.score);
						Ids.add(d.get("ID_POST"));
						resAux.add(d.get("ID_POST"));
						counter += 1;
					}
					// simians.add(simian.calculateSimilarity(snippet, d.get("Code")));
				}
			}
			res.setExpls(expls);
			res.setScores(scores);
			res.setIDsPost(Ids);
			res.setSimianScore(simians);
		}
		return res;
	}

	private void retrieveIndex() {
		if (Files.exists(Paths.get(INDEX_DIRECTORY, "archive.tar.gz"))) {
			logger.info("Index archive found. Skipping download.");
			return;
		} else {
			logger.warn("Couldn't find the Lucene Indexes. I will download and extract it for you (~1.1GB).");
			logger.warn("Downloading archive from {} to {}", REMOTE_INDEX, INDEX_DIRECTORY);
			try {
				FileUtils.copyURLToFile(new URL(REMOTE_INDEX), new File(INDEX_DIRECTORY, "archive.tar.gz"), 5000, 5000);
			} catch (IOException e) {
				logger.error("Couldn't download index archive", e);
			}
		}
		logger.warn("Extracting archive locally");
		try (InputStream fi = Files.newInputStream(Paths.get(INDEX_DIRECTORY, "archive.tar.gz"));
				InputStream bi = new BufferedInputStream(fi);
				InputStream xzi = new GzipCompressorInputStream(bi);
				ArchiveInputStream arch = new TarArchiveInputStream(xzi)) {
			ArchiveEntry entry = null;
			while ((entry = arch.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					File f = new File(entry.getName());
					boolean created = f.mkdir();
					if (!created) {
						logger.error("Unable to create directory {}, during extraction of archive contents.\n",
								f.getAbsolutePath());
					}
				}
				if (!entry.isDirectory()) {
					try (OutputStream o = Files
							.newOutputStream(Paths.get(INDEX_DIRECTORY, entry.getName().replace("index/", "")))) {
						IOUtils.copy(arch, o);
					} catch (IOException e) {
						logger.error("Couldn't write destination file", e);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Couldn't extract dataset archive", e);
		}
	}

	public HashMap<String, Double> entropy(String snippet, ArrayList<HashMap<String, String>> tokens)
			throws IOException, ParseException {
		HashMap<String, Double> entropies = new HashMap<String, Double>();

		for (HashMap<String, String> token : tokens) {
			Collection<String> coll = token.values();
			if (coll.toArray()[0].toString().length() > 1) {
				entropies = entropy.calculate(coll.toArray()[0].toString(), snippet, entropies, tokens.size());
			}
		}

		if (tokens.size() <= prop.getEntThr() || prop.isEntropy() == false) {
			entropies = entropy.assignFlatBoost(entropies);
		} else {
			entropies = entropy.assignBoostLarge(entropies);
		}

		return entropies;
	}
}
