package soRec.Dump2Mongo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class UserHandler extends DefaultHandler {
	private static final Logger logger = LogManager.getLogger(UserHandler.class);
	MongoClient mongoClient;
	MongoDatabase database;
	MongoCollection<Document> collQ;
	MongoCollection<Document> collA;
	
	public UserHandler(String host, int port, String dbname, String collQ, String collA) {
		super();
		mongoClient = new MongoClient(host, port);
		database = mongoClient.getDatabase(dbname);
		this.collQ = database.getCollection(collQ);
		this.collA = database.getCollection(collA);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("row")) {

			String Id = attributes.getValue("Id");
			String Body = attributes.getValue("Body");
			String Tags = attributes.getValue("Tags");
			String Title = attributes.getValue("Title");
			String PostTypeId = attributes.getValue("PostTypeId");
			String ParentId = attributes.getValue("ParentId");
			String AcceptedAnswerId = attributes.getValue("AcceptedAnswerId");
			/*
			 * filter by code tag
			 */
			final org.jsoup.nodes.Document doc = Jsoup.parse(Body, "UTF-8");
			String Code = Utility.getCode(doc);

			if ((Code != "") && (PostTypeId.equals("2"))) // risposta con codice
			{
				String Answer = Utility.getAnswer(doc, Body);
				Document doc2 = new Document("postId", Id).append("code", Code).append("postTypeId", PostTypeId)
						.append("parentId", ParentId).append("acceptedAnswerId", AcceptedAnswerId)
						.append("answer", Answer);
				collA.insertOne(doc2);
			}
			if ((PostTypeId.equals("1")) && !(AcceptedAnswerId == null) && (Tags.contains("<java>"))) // domanda
			{
				String Question = Utility.getQuestion(doc, Body);
				Document doc2 = new Document("postId", Id).append("code", Code).append("title", Title)
						.append("tags", Tags).append("postTypeId", PostTypeId).append("parentId", ParentId)
						.append("acceptedAnswerId", AcceptedAnswerId).append("question", Question);
				collQ.insertOne(doc2);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
	}
}