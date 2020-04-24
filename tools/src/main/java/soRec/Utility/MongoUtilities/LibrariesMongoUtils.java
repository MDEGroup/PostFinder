package soRec.Utility.MongoUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class LibrariesMongoUtils {

	public static void main(String[] args) throws IOException 
	{
		create();
	}

	/*
	 * creation of a mongo collection containing all the libraries to be searched later, NOT the maven libraries list
	 */
	public static void create() throws IOException
	{
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		MongoDatabase database = mongoClient.getDatabase("stackof");
		MongoCollection<Document> coll = database.getCollection("Libraries");
		
		File source = new File("libraries.txt");
		BufferedReader reader = new BufferedReader(new FileReader(source));
		String text = null;

		while ((text = reader.readLine()) != null)
		{
			if(text.length()>=5)
			{
				 Document doc = new Document("Library", text);
		         coll.insertOne(doc);
			}	
		}
		
		reader.close();
		mongoClient.close();
	}
	
}
