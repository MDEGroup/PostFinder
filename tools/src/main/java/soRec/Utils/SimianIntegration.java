package soRec.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.harukizaemon.simian.Checker;
import com.harukizaemon.simian.FileLoader;
import com.harukizaemon.simian.Language;
import com.harukizaemon.simian.Option;
import com.harukizaemon.simian.Options;
import com.harukizaemon.simian.StreamLoader;

public class SimianIntegration {
	private static final Logger logger = LogManager.getLogger(SimianIntegration.class);
	static CodeListenerImpl aulist = new CodeListenerImpl();
	
	public static CodeListenerImpl checkClone(String left, String right, Options options) 
	{
		
		Checker checker = new Checker(aulist, options);
		StreamLoader streamLoader = new StreamLoader(checker);
		FileLoader fileLoader = new FileLoader(streamLoader);
		try {
			fileLoader.load(createTempFile(left));
			fileLoader.load(createTempFile(right));
			checker.check();
		} catch (IOException e) {
			logger.error(e.getMessage());

		}
			
		return aulist;

	}

	private static List<String> getRecommendedlines(ArrayList<String> blocks, String pattern)
	{
		List<String> patternLines = Arrays.asList(pattern.split("\n"));
		patternLines.removeAll(blocks);	
		return patternLines;
	}

	private static File createTempFile(String devSnippet) throws IOException
	{
		File recFile = File.createTempFile("temp", ".java");
		FileWriter writer = new FileWriter(recFile, true);
		writer.write(devSnippet);
		writer.flush();
		writer.close();
		recFile.deleteOnExit();
		return recFile;
	}
	
	public static void writeCsv(PrintWriter pw, CodeListenerImpl aul, Set<String> configuration, org.bson.Document post) throws IOException
	{		
        StringBuilder sb = new StringBuilder();
        String config = "";
        for(String elem:configuration)
        {
        	config += elem+" ";
        }
        

        if(aul.getNumClonedFiles()==2)
        {	
        	sb.append(config);
            sb.append(',');
        	sb.append(aul.getDuplicatedPercentage());
        	sb.append(',');
        	sb.append(post.get("parentId"));
	        sb.append('\n');
	        logger.info(sb.toString());
	        pw.write(sb.toString());
        }
	}
	
	public static int calculateSimilarity(String context, String suggestedPost)
	{
		Options options = new Options();
		options.setThreshold(2);
		options.setOption(Option.REPORT_DUPLICATE_TEXT, true);
		options.setOption(Option.IGNORE_STRINGS, true);
		options.setOption(Option.IGNORE_STRING_CASE, true);
		options.setOption(Option.IGNORE_VARIABLE_NAMES, true);
		options.setOption(Option.IGNORE_CHARACTER_CASE, true);
		options.setOption(Option.IGNORE_IDENTIFIER_CASE, true);
		options.setOption(Option.IGNORE_MODIFIERS, true);
		options.setOption(Option.IGNORE_LITERALS, true);
		options.setOption(Option.LANGUAGE, Language.JAVA);
		options.setOption(Option.IGNORE_SUBTYPE_NAMES, true);
		
		CodeListenerImpl aul = checkClone(context, suggestedPost, options);
		if(aul.getNumClonedFiles()==2)
		{
			return aul.getDuplicatedPercentage();
		}
		else
		{
			return 0;
		}
		
		
	}

}
