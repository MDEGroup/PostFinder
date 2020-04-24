package soRec.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class Jdt {
	private static final Logger logger = LogManager.getLogger(Jdt.class);
	/*
	 * static int K_CLASS_BODY_DECLARATIONS Kind constant used to request that the
	 * source be parsed as a sequence of class body declarations. static int
	 * K_COMPILATION_UNIT Kind constant used to request that the source be parsed as
	 * a compilation unit. static int K_EXPRESSION Kind constant used to request
	 * that the source be parsed as a single expression. static int K_STATEMENTS
	 * Kind constant used to request that the source be parsed as a sequence of
	 * statements.
	 */
	private final static String _FILE_AUX_NAME = "aux.txt";

	public ArrayList<HashMap<String, String>> run(String soSnippet, boolean IndexCreationImportFix,
			HashMap<String, String> classFix, Document doc) {

		ArrayList<HashMap<String, String>> postTokens = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> l = new ArrayList<ArrayList<HashMap<String, String>>>();

		try {
			postTokens = parseKCUnit(soSnippet);
			l.add(postTokens);
			l.add(parseKStatements(soSnippet));
			soSnippet = classFixer(soSnippet);
			l.add(parseKCUnit(soSnippet));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

		for (ArrayList<HashMap<String, String>> ll : l) {
			if (ll.size() >= postTokens.size()) {
				postTokens = ll;
			}
		}

		if (IndexCreationImportFix == true) {
			for (HashMap<String, String> tokens : postTokens) {
				if (tokens.get("ImportDeclaration") != null) {
					break;
				} else {
					int beforeFix = postTokens.size();
					ArrayList<HashMap<String, String>> debugTokens = new ArrayList<HashMap<String, String>>();
					debugTokens.addAll(postTokens);
					try {
						postTokens = classFixerImports(postTokens, classFix, doc);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					if (postTokens.size() > beforeFix) {
						try (FileWriter fw = new FileWriter(_FILE_AUX_NAME, true);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter pw = new PrintWriter(bw);) {
							pw.println("fixed");
							pw.flush();
						} catch (Exception e) {
							logger.error(e.getMessage());
						}
					}

					break;

				}
			}
		}
		return postTokens;
	}

	public HashMap<String, String> createLibFixList() throws IOException
	{
		File source = new File("librariesClassFix.txt");
		HashMap<String,String> libraries = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new FileReader(source));
		String text = null;
		
		while ((text = reader.readLine()) != null)
		{
			text = text.replace("import ","");
			text = text.replace(";","");
			String[] splitted = text.split("\\.");
			String key = splitted[splitted.length-1].replace(";","");
			
			if(libraries.containsKey(key)==true)
			{	
				libraries.put(key, libraries.get(key)+";"+text);
			}
			else
			{
				libraries.put(key, text);
			}
			
		}
		reader.close();
		return libraries;	
	}
	
	public static ArrayList<HashMap<String, String>> classFixerImports(ArrayList<HashMap<String, String>> postTokens, HashMap<String, String> libraries, Document doc) 
	{
		LevenshteinDistance ld = new LevenshteinDistance();

		ArrayList<HashMap<String, String>> exitPostTokens = new ArrayList<HashMap<String, String>>();
		Set<String> set = new HashSet<String>();
		exitPostTokens.addAll(postTokens);

		HashMap<String, String> classInstances = new HashMap<String, String>();
		for(HashMap<String, String> tokens:postTokens)
		{
			String var = tokens.get("ClassInstance");
			if(var!=null) {classInstances.put(var, "ClassInstance");}		
		}
		
		for(HashMap<String, String> tokens:postTokens)
		{
			String var = tokens.get("VariableDeclarationType");
			if(var!=null&&(classInstances.containsKey(var)==false))
			{	
				if(libraries.containsKey(var))
				{
					String imp = libraries.get(var);
					if(imp.contains(";")==false)
					{
						set.add(libraries.get(var));
					}
					else
					{
						String[] elem = imp.split(";");
						String finalImport = "";
						int current=0;
						int max = 0;
						for (String s: elem) 
						{
						    current += ld.apply(s, doc.getString("Title"));
						    current += ld.apply(s, doc.getString("QuestionBody"));
						    current += ld.apply(s, doc.getString("Anwser"));
						    
						    if(current>max)
						    {
						    	finalImport = s;
						    	max=current;
						    	current=0;
						    }
						    else 
						    {current=0;}
						    
						}
						set.add(finalImport);
					}
				}			
			}
		}
		
		
		//System.out.println(ld.apply(libraries.get(""), doc.get("Title").toString()));

		
		for(String elem:set)
		{
			HashMap<String,String> t = new HashMap<String,String>();
			t.put("ImportDeclaration",elem);
			exitPostTokens.add(t);
		}

		return exitPostTokens;	
	}

	public static ArrayList<HashMap<String, String>> parseKCUnit(String snippet) throws IOException {
		final ArrayList<HashMap<String, String>> tokens = new ArrayList<HashMap<String, String>>();

		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(snippet.toCharArray());
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
		parser.setCompilerOptions(options);

		try {
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(new ASTVisitor() {
				public boolean visit(final LineComment commentNode) {

					return false;
				}

				public boolean visit(ImportDeclaration node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("ImportDeclaration", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(MethodDeclaration node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("MethodDeclaration", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(MethodInvocation node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("MethodInvocation", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(VariableDeclarationStatement node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("VariableDeclarationType", node.getType().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(VariableDeclarationFragment node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("VariableDeclaration", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(ClassInstanceCreation node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("ClassInstance", node.getType().toString());
					tokens.add(token);
					return true;
				}

			});

		} catch (Exception exc) {
			logger.error("JDT parsing error: {}", exc.getMessage());
		}

		return tokens;

	}

	public static ArrayList<HashMap<String, String>> parseKStatements(String snippet) throws IOException {

		final ArrayList<HashMap<String, String>> tokens = new ArrayList<HashMap<String, String>>();

		@SuppressWarnings("deprecation")
		ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setBindingsRecovery(true);
		Map<String, String> options = JavaCore.getOptions();
		parser.setCompilerOptions(options);
		parser.setUnitName("test");

		String src = snippet;
		String[] sources = {};
		String[] classpath = { "/usr/lib/jvm/java-8-openjdk-amd64" };

		parser.setEnvironment(classpath, sources, new String[] {}, true);
		parser.setSource(src.toCharArray());

		try {

			final Block block = (Block) parser.createAST(null);

			block.accept(new ASTVisitor() {
				public boolean visit(final LineComment commentNode) {

					return false;
				}

				public boolean visit(ImportDeclaration node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("ImportDeclaration", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(MethodDeclaration node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("MethodDeclaration", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(MethodInvocation node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("MethodInvocation", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(VariableDeclarationStatement node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("VariableDeclarationType", node.getType().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(VariableDeclarationFragment node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("VariableDeclaration", node.getName().toString());
					tokens.add(token);
					return true;
				}

				public boolean visit(ClassInstanceCreation node) {
					final HashMap<String, String> token = new HashMap<String, String>();
					token.put("ClassInstance", node.getType().toString());
					tokens.add(token);
					return true;
				}

			});
		}

		catch (Exception exc) {
			logger.error("JDT parsing error: {}", exc.getMessage());
		}

		return tokens;

	}

	public ArrayList<HashMap<String, String>> cleanDuplicates(ArrayList<HashMap<String, String>> tokens) {

		Set<HashMap<String, String>> set = new HashSet<HashMap<String, String>>();
		set.addAll(tokens);
		tokens.clear();
		tokens.addAll(set);

		return tokens;
	}

	public static String classFixer(String snippet) {

		/*
		 * wrapping
		 */

		String fix = "public class fix{ ";
		fix += snippet + "}";

		return fix;
	}

}
