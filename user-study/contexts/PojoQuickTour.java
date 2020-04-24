/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.poi.ss.formula.functions.Address;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * The POJO QuickTour code example
 */
public class PojoQuickTour {
    /**
     * Run this main method to see the output of this quick example.
     *
     * @param args takes an optional single argument for the connection string
     * @throws InterruptedException if a latch is interrupted
     */
    public static void main(final String[] args) throws InterruptedException {
        MongoClient mongoClient;

        if (args.length == 0) {
            // connect to the local database server
            mongoClient = MongoClients.create();
        } else {
            mongoClient = MongoClients.create(args[0]);
        }

        // create codec registry for POJOs
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // get handle to "mydb" database
        MongoDatabase database = mongoClient.getDatabase("mydb").withCodecRegistry(pojoCodecRegistry);

        // get a handle to the "people" collection
        final MongoCollection<Person> collection = database.getCollection("people", Person.class);

        // drop all the data in it
        final CountDownLatch dropLatch = new CountDownLatch(1);
        collection.drop(new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                dropLatch.countDown();
            }
        });
        dropLatch.await();

        // make a document and insert it
        final Person ada = new Person("Ada Byron", 20, new Address("St James Square", "London", "W1"));
        System.out.println("Original Person Model: " + ada);

        collection.insertOne(ada, new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                // Person will now have an ObjectId
                System.out.println("Mutated Person Model: " + ada);
                System.out.println("Inserted!");
            }
        });

        // get it (since it's the only one in there since we dropped the rest earlier on)
        SingleResultCallback<Person> printCallback = new SingleResultCallback<Person>() {
            @Override
            public void onResult(final Person person, final Throwable t) {
                System.out.println(person);
            }
        };
        collection.find().first(printCallback);

        // now, lets add some more people so we can explore queries and cursors
        List<Person> people = asList(
                new Person("Charles Babbage", 45, new Address("5 Devonshire Street", "London", "W11")),
                new Person("Alan Turing", 28, new Address("Bletchley Hall", "Bletchley Park", "MK12")),
                new Person("Timothy Berners-Lee", 61, new Address("Colehill", "Wimborne", null))
        );

        final CountDownLatch countLatch = new CountDownLatch(1);
        collection.insertMany(people, new SingleResultCallback<Void>() {
            @Override
            public void onResult(final Void result, final Throwable t) {
                collection.countDocuments(new SingleResultCallback<Long>() {
                    @Override
                    public void onResult(final Long count, final Throwable t) {
}}}}}}