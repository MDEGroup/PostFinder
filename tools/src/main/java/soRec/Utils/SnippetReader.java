package soRec.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SnippetReader 
{
	
	public String readFromFile(File file) throws IOException 
	{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		CodeCleaner cc = new CodeCleaner();
		String snippet;
		snippet = cc.commentsFixer(br);
		
		return snippet;
	}

}
