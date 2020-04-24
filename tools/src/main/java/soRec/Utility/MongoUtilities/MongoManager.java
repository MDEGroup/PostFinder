package soRec.Utility.MongoUtilities;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import soRec.Utils.Properties;

public class MongoManager {
	private static final Logger logger = LogManager.getLogger(MongoManager.class);
	Properties prop = new Properties();

	MongoClient mongoClient;
	MongoDatabase database;
	int limit;
	MongoCollection<Document> collA;
	MongoCollection<Document> collQ;
	MongoCollection<Document> collF;

	public MongoManager(String host, int port, String dbname, String collA, String collQ, String collF) {
		mongoClient = new MongoClient(host, port);
		database = mongoClient.getDatabase(dbname);
		this.collF = database.getCollection(collF);
		limit = 1200000;
		this.collA = database.getCollection(collA);
		this.collQ = database.getCollection(collQ);
		this.collF = database.getCollection(collF);
	}

	public ArrayList<Document> loadAll() {
		int count = 0;
		ArrayList<Document> list = new ArrayList<Document>();
		// {"username" : {$regex : ".*son.*"}}
		FindIterable<Document> docs = collF.find();

		for (Document doc : docs) {
			if (count >= limit) {
				break;
			}
			if (doc.getString("code") != null) {
				list.add(doc);
			}
			count += 1;
		}
		return list;
	}

	public ArrayList<Document> loadLimited(Properties prop) {
		ArrayList<Document> list = new ArrayList<Document>();

		int aux = prop.getMongoLimitValue();
		FindIterable<Document> docs = collF.find().limit(prop.getMongoLimitValue()).skip(prop.getMongoSkipValue());
		prop.setMongoSkipValue(prop.getMongoSkipValue() + aux);

		for (Document doc : docs) {
			if (doc.getString("code") != null) {
				list.add(doc);
			}
		}
		docs = null;
		logger.info(list.size());
		return list;
	}

	public void search() {
		Bson bsonFilter;

		FindIterable<Document> docs = collQ.find();
		for (Document doc : docs) {
			try {
				String parentId = doc.getString("postId");
				bsonFilter = Filters.eq("parentId", parentId);

				FindIterable<Document> results = collA.find(bsonFilter);

				if (results != null) // se l'accepted id è presente
				{
					for (Document result : results) {
						result.append("Title", doc.getString("title"));
						result.append("QuestionBody", doc.getString("question"));
						result.append("Answer", doc.getString("answer"));
						collF.insertOne(result);
					}
				}
			}
			catch (Exception exc) {
				logger.error(exc.getMessage());
			}
		}

	}

}
