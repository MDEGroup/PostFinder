package soRec.Utils;

import java.util.ArrayList;

import org.apache.lucene.search.Explanation;

public class ResultsModel {
	
	private String query;
	private String fileName;
	private long totalHits;
	private ArrayList<Explanation> expls;
	private ArrayList<Float> scores;
	private ArrayList<String> IDsPost;
	private ArrayList<Integer> simianScore;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(long totalHits) {
		this.totalHits = totalHits;
	}
	public ArrayList<Explanation> getExpls() {
		return expls;
	}
	public void setExpls(ArrayList<Explanation> expls) {
		this.expls = expls;
	}
	public ArrayList<Float> getScores() {
		return scores;
	}
	public void setScores(ArrayList<Float> scores) {
		this.scores = scores;
	}
	public ArrayList<String> getIDsPost() {
		return IDsPost;
	}
	public void setIDsPost(ArrayList<String> iDsPost) {
		IDsPost = iDsPost;
	}
	public ArrayList<Integer> getSimianScore() {
		return simianScore;
	}
	public void setSimianScore(ArrayList<Integer> simianScore) {
		this.simianScore = simianScore;
	}
}
