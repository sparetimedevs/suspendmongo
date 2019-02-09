package com.sparetimedevs.suspendmongo

import com.mongodb.reactivestreams.client.MongoCollection

inline fun <reified T: Any> getCollection(database: Database): MongoCollection<T> =
		database.mongoDatabase.getCollection(T::class.java.simpleName!!, T::class.java)
