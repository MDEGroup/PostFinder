package soRec.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class FileUtils {
	
	public ArrayList<ArrayList<HashMap<String,String>>>  getTokensFromProject(File dirpath, String type,  ArrayList<ArrayList<HashMap<String,String>>>  result) throws IOException {
		
		File[] list=dirpath.listFiles();
		Jdt jdtParser = new Jdt();
		
		for(File f: list) {
			if(f.isDirectory()) {
				getTokensFromProject(f,type,result);			
			}
			if(f.isFile()) {
				String ext = FilenameUtils.getExtension(f.getName());				
				if(ext.equals(type)) {					
					String snippet = new String (Files.readAllBytes(Paths.get(f.getPath())));					
					result.add(jdtParser.run(snippet,false,null,null));
				} 				
			}
		}			
	
	return result;
	
	}
	
	public ArrayList<String> splitFile(String filePath,String context, String left) throws IOException{
		List<String> result=new ArrayList<String>();		
		File file=new File(filePath);
		int begin=0;		
		List<String> lines=Files.readAllLines(Paths.get(file.getPath()));
		int end=lines.size()/2;
		
		FileWriter writeContext=new FileWriter(new File(context));
		FileWriter writeLeftlover=new FileWriter(new File(left));
		
		if(begin>lines.size()) {
			begin=lines.size()-10;
		}
				
		
		for(int i=begin;i<end;i++) {
							
			result.add(lines.get(i));			
			writeContext.write(lines.get(i)+"\n");
		}		
		writeContext.flush();
		writeContext.close();
		
		for(int i=end;i<lines.size();i++) {
			writeLeftlover.write(lines.get(i)+"\n");
		}		
		writeLeftlover.flush();
		writeLeftlover.close();	
				
		return null;
	}
	
	
	

}
