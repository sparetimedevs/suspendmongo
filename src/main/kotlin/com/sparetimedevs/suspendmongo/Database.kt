package com.sparetimedevs.suspendmongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

class Database(connectionString: String, name: String) {

	val mongoDatabase: MongoDatabase

	init {
		val pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
		val settings = MongoClientSettings.builder()
				.applyConnectionString(ConnectionString(connectionString))
				.codecRegistry(pojoCodecRegistry)
				.build()
		val client = MongoClients.create(settings)
		mongoDatabase = client.getDatabase(name)
	}
}
