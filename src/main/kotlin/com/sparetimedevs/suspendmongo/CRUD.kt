package com.sparetimedevs.suspendmongo

import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.Success.SUCCESS
import kotlinx.coroutines.channels.toCollection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.openSubscription
import org.bson.conversions.Bson
import org.bson.types.ObjectId

suspend inline fun <reified T : Any> MongoCollection<T>.getAll(): ArrayList<T> =
    this.find().openSubscription().toCollection(ArrayList())

suspend inline fun <reified T : Any> MongoCollection<T>.getOneById(id: ObjectId): T {
    val filter = idFilterQuery(id)
    return this.find(filter).awaitFirstOrNull() ?: throw NotFound("No entity found, while 1 was expected.")
}

suspend inline fun <reified T : Any> MongoCollection<T>.deleteAll(): Boolean {
    val publisher = this.drop()
    val success = publisher.awaitFirstOrNull()
    return success == SUCCESS
}

suspend inline fun <reified T : Any> MongoCollection<T>.deleteOneById(id: ObjectId): T {
    val filter = idFilterQuery(id)
    val publisher = this.findOneAndDelete(filter)
    return publisher.awaitFirstOrNull() ?: throw NotFound("No entity found, while 1 was expected.")
}

suspend inline fun <reified T : Any> MongoCollection<T>.save(entity: T): Boolean {
    val publisher = this.insertOne(entity)
    val success = publisher.awaitFirstOrNull()
    return success == SUCCESS
}

suspend inline fun <reified T : Any> MongoCollection<T>.update(id: ObjectId, entity: T): Boolean {
    val filter = idFilterQuery(id)
    val publisher = this.findOneAndReplace(filter, entity)
    val updateResult = publisher.awaitFirstOrNull()
    return updateResult != null
}

fun idFilterQuery(id: ObjectId): Bson = Filters.eq("_id", id)

class NotFound(message: String) : RuntimeException(message)
