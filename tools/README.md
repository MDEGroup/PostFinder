# SOrec
This folder stores the SOrec implementation written in Java. We provide you with some neccessary commands to run SOrec on the attached datasets.


## Usage
```
usage: SOrec
 -indexFolder <indexFolder>   The path to where Lucene index is stored.
                              SOrec downloads preloaded lucene index if
                              the folder index is empty or doesn't exist.
 -queryFolder <queryFolder>   The path to where snippets of code are
                              stored.
```

## Example of output
```
...
###############
MongoHelloWorld.java
ImportDeclaration: com.mongodb.DB^1.0 OR MethodInvocation: insert^1.0 OR VariableDeclaration: table^1.0 OR MethodInvocation: put^1.0 OR MethodInvocation: getDB^1.0 OR MethodInvocation: next^1.0 OR ClassInstance: MongoClient^1.0 OR ClassInstance: BasicDBObject^1.0 OR ImportDeclaration: com.mongodb.DBCurs1.0 OR VariableDeclaration: searchQuery^1.0 OR ImportDeclaration: com.mongodb.MongoClient^1.0 OR ImportDeclaration: com.mongodb.BasicDBObject^1.0 OR ImportDeclaration: com.mongodb.DBCollection^1.0 OR MethodInvocation: println^1.0 OR VariableDeclaration: mongo^1.0 OR VariableDeclaration: curs1.0 OR MethodInvocation: find^1.0 OR VariableDeclaration: document^1.0 OR VariableDeclarationType: DB^1.0 OR MethodInvocation: hasNext^1.0 OR VariableDeclaration: db^1.0 OR MethodInvocation: getCollection^1.0 OR MethodDeclaration: main^1.0 OR VariableDeclarationType: MongoClient^1.0 OR VariableDeclarationType: DBCollection^1.0 OR VariableDeclarationType: BasicDBObject^1.0 OR VariableDeclarationType: DBCurs1.0 OR ClassInstance: Date^1.0 OR ImportDeclaration: java.util.Date^1.0 OR Answer: util^0.3333333333333333 OR Answer: mongodb^1.4 OR Answer: Date^0.3333333333333333 OR Question: util^0.3333333333333333 OR Question: mongodb^1.4 OR Question: Date^0.3333333333333333 OR Title: util^1 OR Title: mongodb^4.0 OR Title: Date^1
	https://stackoverflow.com/questions/18341090
	https://stackoverflow.com/questions/32867648
	https://stackoverflow.com/questions/22361505
	https://stackoverflow.com/questions/12670195
	https://stackoverflow.com/questions/28209125
###############
QuartzExample.java
ImportDeclaration: org.apache.camel.impl.DefaultCamelContext^1.0 OR ImportDeclaration: org.apache.camel.CamelContext^1.0 OR MethodDeclaration: main^1.0 OR ImportDeclaration: org.apache.camel.builder.RouteBuilder^1.0 OR Answer: apache^1.0 OR Answer: camel^1.0 OR Question: apache^1.0 OR Question: camel^1.0 OR Title: apache^3 OR Title: camel^3
	https://stackoverflow.com/questions/46766311
	https://stackoverflow.com/questions/45700257
	https://stackoverflow.com/questions/36928962
	https://stackoverflow.com/questions/43077109
	https://stackoverflow.com/questions/37550295
...

```

## Examples

The following Maven command is used to run SOrec on a set of queries stored in the contexts folder:

```
mvn exec:java -Dexec.mainClass="soRec.Main" -Dexec.args="-indexFolder /path/to/luceneIndex -queryFolder /path/to/codeContexts"
```


## Index creation
### Usage


```
usage: SORec - Indexer
 -dbhost <dbhost>             Monmgodb host (localhost)
 -dbname <dbname>             Monmgodb port (stackof)
 -dbport <dbport>             Monmgodb port (27017)
 -indexFolder <indexFolder>   The path to where Lucene indexes are stored.
 -postdump <postdump>         The path to where SO dump index is stored.
                              SOrec_Recommender downloads preloaded so
                              dumps (700MB) if the file index doesn't
                              exist.
```
### Examples

From Maven:

```
mvn exec:java -Dexec.mainClass="soRec.Dump2Mongo.Main" -Dexec.args="-dbhost localhost -dbname stackof -dbport 27017 -indexFolder /path/to/luceneIndex -postdump /path/to/localdump"
```
### Note

To run the Indexer module, you need MongoDB and a StackOverflow local dump. Due to space constraints, we loaded to the repository a smaller dump of 700MB at your disposal. If you want to replicate the whole experiments, it is necessary to 
download the entire SO dump of 70 GB.
