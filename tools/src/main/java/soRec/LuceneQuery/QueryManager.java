package soRec.LuceneQuery;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import soRec.Utils.ImportFinder;
import soRec.Utils.Properties;

public class QueryManager 
{		
	private static final Logger logger = LogManager.getLogger(QueryManager.class);

	public String CreateBoostedQuery(Set<String> config, ArrayList<HashMap<String, String>> tokens, ArrayList<String> imports, HashMap<String, Double> entropies, ArrayList<String> localImportList, Properties prop) throws IOException
	{    	
		SingleQuery sq = new SingleQuery();
		
		String query = "";
		boolean firstGuard = true;
		
		for(HashMap<String, String> token : tokens)
		{		
			if(config.contains("ImportDeclarationOR"))
			{
				query = sq.getImportDeclarationBoosted(token, query, firstGuard, true, "OR", entropies);
				if(query.indexOf("AND")==-1&&query.length()>2||query.indexOf("OR")==-1&&query.length()>2)
				{
					firstGuard = false;
				}
			}
			if(config.contains("MethodDeclarationOR"))
			{
				query = sq.getMethodDeclarationBoosted(token, query, firstGuard, true, "OR", entropies);
				if(query.indexOf("AND")==-1&&query.length()>2||query.indexOf("OR")==-1&&query.length()>2)
				{
					firstGuard = false;
				}
			}
			if(config.contains("MethodInvocationOR"))
			{
				query = sq.getMethodInvocationBoosted(token, query, firstGuard, true, "OR", entropies);
				if(query.indexOf("AND")==-1&&query.length()>2||query.indexOf("OR")==-1&&query.length()>2)
				{
					firstGuard = false;
				}
			}
			if(config.contains("VariableDeclarationOR"))
			{
				query = sq.getVariableDeclarationBoosted(token, query, firstGuard, true, "OR", entropies);
				if(query.indexOf("AND")==-1&&query.length()>2||query.indexOf("OR")==-1&&query.length()>2)
				{
					firstGuard = false;
				}
			}

			if(config.contains("VariableDeclarationTypeOR"))
			{
				query = sq.getVariableDeclarationTypeBoosted(token, query, firstGuard, true, "OR", imports, entropies);
				if(query.indexOf("AND")==-1&&query.length()>2||query.indexOf("OR")==-1&&query.length()>2)
				{
					firstGuard = false;
				}
			}
			if(config.contains("ClassInstanceOR"))
			{
				query = sq.getClassInstanceBoosted(token, query, firstGuard, true, "OR", entropies);
				if(query.indexOf("AND")==-1&&query.length()>2||query.indexOf("OR")==-1&&query.length()>2)
				{
					firstGuard = false;
				}
			}
		}
		
		/*
		 * if true search the library to populate title, question and answer
		 */
		if(prop.isLib())
		{
	    	ImportFinder impf = new ImportFinder();
	    	HashMap<String, Integer> vars = impf.Find(tokens,localImportList);
			query = sq.getAnswer(vars, query, true, "OR",prop);
			query = sq.getQuestion(vars, query, true, "OR",prop);
			query = sq.getTitle(vars, query, true, "OR",prop);
		}
		
		return query;	
	}
	
	
	public TopDocs query(String[] args, String INDEX_DIRECTORY, String querystr, Properties prop)
			throws IOException, ParseException {

		Directory indexDir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		Analyzer analzer = new StandardAnalyzer();

		try 
		{
			List<String> fields2 = getAllIndexTags(INDEX_DIRECTORY);
			String[] fields = new String[fields2.size()];
			int i = 0;
			for (String string : fields2) 
			{
				fields[i] = string;
				i++;
			}
			MultiFieldQueryParser qp = new MultiFieldQueryParser(fields, analzer);
			Query q = qp.parse(querystr);
			
			prop.setQ(q);
			
			int hitsPerPage = prop.getHitsPerPage();
			IndexReader reader = DirectoryReader.open(indexDir);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			/*
			 * lucene ranking method config
			 */
			if(prop.isBm25()==false)
			{
				ClassicSimilarity CS = new ClassicSimilarity();
				searcher.setSimilarity(CS);
			}
			/*
			 * 
			 */
			
			TopDocs docs = searcher.search(q, hitsPerPage);
			return docs;
		}

		catch (Exception e) 
		{
			
			 logger.error(e);
			 
			return null;
		}

	}

	
	public static List<String> getAllIndexTags(String INDEX_DIRECTORY) 
	{
		Collection<String> result = new HashSet<String>();
		try {
			IndexReader luceneIndexReader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_DIRECTORY)));
			result = MultiFields.getIndexedFields(luceneIndexReader);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		List<String> sortedList = new ArrayList<String>(result);
		Collections.sort(sortedList);

		return sortedList;
	}
	
}
