package soRec.Dump2Mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

public class Utility {
	private static final Logger logger = LogManager.getLogger(Utility.class);

	public static String getCode(Document doc) {
		String Code = "";

		try {

			for (Element result : doc.select("code")) {
				Code = Code + ((TextNode) result.childNode(0)).getWholeText();
			}
		}

		catch (Exception exc) {
		}

		return Code;

	}

	public static String getQuestion(Document doc, String body) {
		String Question = body;
		try {

			for (Element result : doc.select("code")) {
				Question = Question.replace(((TextNode) result.childNode(0)).getWholeText(), "");
			}
		}

		catch (Exception exc) {
		}
		Question = Question.replace("<code>", "");
		Question = Question.replace("</code>", "");
		return Question;
	}

	public static String getAnswer(Document doc, String body) {
		String Answer = body;
		try {

			for (Element result : doc.select("code")) {
				Answer = Answer.replace(((TextNode) result.childNode(0)).getWholeText(), "");
			}
		}

		catch (Exception exc) {
		}
		Answer = Answer.replace("<code>", "");
		Answer = Answer.replace("</code>", "");
		return Answer;
	}

}
