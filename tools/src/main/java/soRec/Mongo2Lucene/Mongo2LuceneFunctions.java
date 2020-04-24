package soRec.Mongo2Lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import soRec.Utils.Jdt;

public class Mongo2LuceneFunctions 
{
	
	public Document addTokens(Jdt jdtParser, org.bson.Document elem, HashMap<String,String> classFix) throws IOException 
	{
		String snippet = "";
		String postId = "";
		ArrayList<HashMap<String, String>> tokens = new ArrayList<HashMap<String, String>>();

		postId = (String) (elem.get("parentId"));
		snippet = (String) (elem.get("code"));

		/*
		 * rimozione delle linee che contengono "//"
		 */
		String snippetN = "";
		String[] lines = snippet.split(System.getProperty("line.separator"));
		for (String line : lines) {
			if (line.contains("//")) {
				continue;
			} else {
				snippetN += line;
			}
		}

		tokens = jdtParser.run(snippetN,true,classFix, elem);

		/*
		 * INSERIMENTO VALORI NEL DOC LUCENE
		 */
		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		Field idPost = new TextField("ID_POST", postId, Field.Store.YES);
		doc.add(idPost);

		for (HashMap<String, String> token : tokens) {
			String value = (String) token.get("ImportDeclaration");
			if (value != null) {
				Field field = new TextField("ImportDeclaration", value, Field.Store.YES);
				doc.add(field);
			}

			value = (String) token.get("MethodDeclaration");
			if (value != null) {
				Field field = new TextField("MethodDeclaration", value, Field.Store.YES);
				doc.add(field);
			}

			value = (String) token.get("MethodInvocation");
			if (value != null) {
				Field field = new TextField("MethodInvocation", value, Field.Store.YES);
				doc.add(field);
			}

			value = (String) token.get("VariableDeclarationType");
			if (value != null) {
				Field field = new TextField("VariableDeclarationType", value, Field.Store.YES);
				doc.add(field);
			}

			value = (String) token.get("VariableDeclaration");
			if (value != null) {
				Field filed = new TextField("VariableDeclaration", value, Field.Store.YES);
				doc.add(filed);
			}

			value = (String) token.get("ClassInstance");
			if (value != null) {
				Field field = new TextField("ClassInstance", value, Field.Store.YES);
				doc.add(field);
			}
		}
		
		return doc;
	}

	public Document addTitle(Document doc, org.bson.Document elem)
	{
		String value = (String) (elem.get("Title"));
		Field field = new TextField("Title", value, Field.Store.YES);
		doc.add(field);
		
		return doc;
	}
	
	public Document addQuestionDiscussion(Document doc, org.bson.Document elem)
	{
		String value = (String) (elem.get("QuestionBody"));
		Field field = new TextField("Question", value, Field.Store.YES);
		doc.add(field);
		
		return doc;
	}
	
	public Document addAnswerDiscussion(Document doc, org.bson.Document elem)
	{
		String value = (String) (elem.get("answer"));
		Field field = new TextField("Answer", value, Field.Store.YES);
		doc.add(field);
		
		return doc;
	}
	
	public Document addCode(Document doc, org.bson.Document elem)
	{
		String value = (String) (elem.get("code"));
		Field field = new TextField("Code", value, Field.Store.YES);
		doc.add(field);
		
		return doc;
	}
	
}
