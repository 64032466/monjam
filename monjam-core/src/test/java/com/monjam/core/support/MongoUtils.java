package com.monjam.core.support;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.monjam.core.command.DbMigrateTransactionIT;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MongoUtils {
    public static List<Document> find(MongoDatabase database, String collection, Bson sort) {
        List<Document> documents = new ArrayList<>();
        try (MongoCursor<Document> cursor = database.getCollection(collection).find().sort(sort).iterator()) {
            while (cursor.hasNext()) {
                documents.add(cursor.next());
            }
        }
        return documents;
    }

    public static void insert(MongoDatabase database, String collectionName, Document document) {
        database.getCollection(collectionName).insertOne(document);
    }

    public static void update(MongoDatabase database, String collectionName, Bson filter, Bson update) {
        database.getCollection(collectionName).updateMany(filter, update);
    }

    public static void importToCollectionFromFile(MongoDatabase database, String collectionName, String filePath) throws Exception {
        Path path = Paths.get(DbMigrateTransactionIT.class.getClassLoader().getResource(filePath).toURI());
        for (BsonValue value : BsonArray.parse(new String(Files.readAllBytes(path)))) {
            database.getCollection(collectionName, BsonDocument.class).insertOne(value.asDocument());
        }
    }

    public static void truncateCollection(MongoDatabase database, String collectionName) {
        database.getCollection(collectionName).deleteMany(new Document());
    }
}
