package soRec.Utils;

import org.apache.lucene.search.Query;

import com.google.common.collect.ImmutableSet;

public class Properties {
	private String INDEX_DIRECTORY = ""; //lucene indexes directory
	//private File folder = new File(""); //snippets main directory
	private ImmutableSet<String> config = ImmutableSet.of("VariableDeclarationTypeOR", "ImportDeclarationOR", "MethodDeclarationOR", "ClassInstanceOR", "VariableDeclarationOR", "MethodInvocationOR"); // query tokens configuration
	//private String[] classpath = {""}; //dir jdk, used by jdt
	private int luceneTreshold = 5; //from lucene results, we consider the first five
	private int lowerBoundLuceneTreshold = 1; // we consider only the results with at least 1 result
	private int upperBoundLuceneTreshold = 1000; // we consider only the results with at most 1000 results
	private int hitsPerPage = 1200000; // maximum quantity of documents considered by lucene
	private int limit = 1200000; // maximum quantity of documents considered by mongo
	private int mongoSkipValue = 0; // used with mongoLimitValue
	private int mongoLimitValue = 100000; // can't load the whole dataset from mongo, so we go 100000 by 100000
	private String mongoDbName = ""; // stackoverflow db name
	private String finalCollection = ""; // stackoverflow collection name
	private Query q = null;
	private boolean bm25 = true; // bm25 or td-idf
	private boolean entropy = true;// entropy or not
	private boolean lib = true;// using the library searching in the query
	private int entThr = 30; // tokens threshold for entropy usage
	private double titleBoostValue = 4.0;// fixed boosting values
	private double questionBoostValue = 1.4;
	private double answerBoostValue = 1.4;

	public double getTitleBoostValue() {
		return titleBoostValue;
	}
	public void setTitleBoostValue(double titleBoostValue) {
		this.titleBoostValue = titleBoostValue;
	}
	public double getQuestionBoostValue() {
		return questionBoostValue;
	}
	public void setQuestionBoostValue(double questionBoostValue) {
		this.questionBoostValue = questionBoostValue;
	}
	public double getAnswerBoostValue() {
		return answerBoostValue;
	}
	public void setAnswerBoostValue(double answerBoostValue) {
		this.answerBoostValue = answerBoostValue;
	}
	public String getINDEX_DIRECTORY() {
		return INDEX_DIRECTORY;
	}
	public void setINDEX_DIRECTORY(String iNDEX_DIRECTORY) {
		INDEX_DIRECTORY = iNDEX_DIRECTORY;
	}
//	public File getFolder() {
//		return folder;
//	}
//	public void setFolder(File folder) {
//		this.folder = folder;
//	}
	public ImmutableSet<String> getConfig() {
		return config;
	}
	public void setConfig(ImmutableSet<String> config) {
		this.config = config;
	}
	public int getLuceneTreshold() {
		return luceneTreshold;
	}
	public void setLuceneTreshold(int luceneTreshold) {
		this.luceneTreshold = luceneTreshold;
	}
	public int getLowerBoundLuceneTreshold() {
		return lowerBoundLuceneTreshold;
	}
	public void setLowerBoundLuceneTreshold(int lowerBoundLuceneTreshold) {
		this.lowerBoundLuceneTreshold = lowerBoundLuceneTreshold;
	}
	public int getUpperBoundLuceneTreshold() {
		return upperBoundLuceneTreshold;
	}
	public void setUpperBoundLuceneTreshold(int upperBoundLuceneTreshold) {
		this.upperBoundLuceneTreshold = upperBoundLuceneTreshold;
	}
	public int getHitsPerPage() {
		return hitsPerPage;
	}
	public void setHitsPerPage(int hitsPerPage) {
		this.hitsPerPage = hitsPerPage;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getMongoDbName() {
		return mongoDbName;
	}
	public void setMongoDbName(String mongoDbName) {
		this.mongoDbName = mongoDbName;
	}
	public String getFinalCollection() {
		return finalCollection;
	}
	public void setFinalCollection(String finalCollection) {
		this.finalCollection = finalCollection;
	}
	public int getMongoSkipValue() {
		return mongoSkipValue;
	}
	public void setMongoSkipValue(int mongoSkipValue) {
		this.mongoSkipValue = mongoSkipValue;
	}
	public int getMongoLimitValue() {
		return mongoLimitValue;
	}
	public void setMongoLimitValue(int mongoLimitValue) {
		this.mongoLimitValue = mongoLimitValue;
	}
	public Query getQ() {
		return q;
	}
	public void setQ(Query q) {
		this.q = q;
	}
	public boolean isBm25() {
		return bm25;
	}
	public void setBm25(boolean bm25) {
		this.bm25 = bm25;
	}
	public int getEntThr() {
		return entThr;
	}
	public void setEntThr(int entThr) {
		this.entThr = entThr;
	}
	public boolean isEntropy() {
		return entropy;
	}
	public void setEntropy(boolean entropy) {
		this.entropy = entropy;
	}
	public boolean isLib() {
		return lib;
	}
	public void setLib(boolean lib) {
		this.lib = lib;
	}

}
