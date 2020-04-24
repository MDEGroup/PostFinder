package soRec.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeCleaner {
	
	public String squareRemover(String query)
	{
		query = query.replace("[]", "");
		query = query.replace("OR^", "");
		query = query.replace("or^", "");
		query = query.replace("AND^", "");
		query = query.replace("and^", "");
		return query;
	}
	
	public String extraCharacterRemover(String target) 
	{
		String newTarget=target;
		if(target.length()>0&&Character.isDigit(target.charAt(0)))
		{
			newTarget = target.substring(2);
		}
		
		return newTarget;
	}
	
	public String commentsFixer(BufferedReader br) throws IOException
	{
		String snippet = "";
		String line;
		
		while((line = br.readLine())!=null)
		{
			line = extraCharacterRemover(line);
			if(line.contains("//"))
			{
				continue;
			}
			else
			{
				snippet += line+" \n";
			}
		}
		return snippet;
	}
	
	public ArrayList<String> varDeclTypeFix(ArrayList<HashMap<String, String>> tokens)
	{
		/*
		 * vardecltype fix
		 */
			ArrayList<String> imports = new ArrayList<String>();
			for(HashMap<String, String> token : tokens)
			{	
				if(token.get("ImportDeclaration")!=null)
				{
					String imp = (String) token.get("ImportDeclaration");
					imp = imp.substring(imp.lastIndexOf(".") + 1);
					imports.add(imp);
				}
			}
			
		return imports;
	}

}
