package soRec.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ImportFinder {
	
	public HashMap<String, Integer> Find(ArrayList<HashMap<String, String>> tokens, ArrayList<String> localImportList) throws IOException
	{
		ArrayList<String> importedTokens = new ArrayList<String>();
		for(HashMap<String, String> token : tokens)
		{
			String t = token.get("ImportDeclaration");
			if(t!=null)
			{
				String[] splitted = t.split("\\.");
				for (String line : splitted) 
				{
					for(String imp:localImportList)
					{
						if(imp.toLowerCase().equals(line.toLowerCase()))
						{
							importedTokens.add(line);
							break;
						}
					}
				}
			}

		}
		
		HashMap<String, Integer> exitTokens = new HashMap<String, Integer>();
		for(String elem:importedTokens)
		{
			if(exitTokens.containsKey(elem))
			{
				exitTokens.put(elem, exitTokens.get(elem)+1);
			}
			else
			{
				exitTokens.put(elem,1);
			}
		}
		
		
		return exitTokens;
		
	}
	
	public ArrayList<String> createLocalImportList() throws IOException
	{
		ArrayList<String> ImportList = new ArrayList<String>();
		File source = new File("libraries.txt");
		BufferedReader reader = new BufferedReader(new FileReader(source));
		String text = null;
		
		while ((text = reader.readLine()) != null)
		{
			if(text.length()>=5)
			{
				ImportList.add(text.replace("/", ""));
			}
		}
		reader.close();
		return ImportList;
	}

}
