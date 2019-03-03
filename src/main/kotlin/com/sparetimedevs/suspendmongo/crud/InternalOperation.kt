/*
 * Copyright (c) 2019 sparetimedevs and respective authors and developers.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sparetimedevs.suspendmongo.crud

import com.mongodb.reactivestreams.client.Success.SUCCESS
import com.sparetimedevs.suspendmongo.Collection
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.suspendmongo.result.Result
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.consumeEach
import org.bson.conversions.Bson

//TODO create better error messages.

@PublishedApi
internal suspend fun <T: Any> createOneSuspendMongoResult(collection: Collection<T>, entity: T): Result<Error, T> {
    val mongoCollection = collection.getCollection()
    return try {
        val publisher = mongoCollection.insertOne(entity)
        val success = publisher.awaitFirstOrNull()
        when (success) {
            SUCCESS -> Result.Success(entity)
            else -> Result.Failure(Error.UnknownError())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@PublishedApi
internal suspend fun <T: Any> readOneSuspendMongoResult(collection: Collection<T>, filter: Bson): Result<Error, T> {
    val mongoCollection = collection.getCollection()
    return try {
        val resultEntity = mongoCollection.find(filter).awaitFirstOrNull()
        when {
            resultEntity != null -> Result.Success(resultEntity)
            else -> Result.Failure(Error.EntityNotFound())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@PublishedApi
internal suspend fun <T: Any> readAllSuspendMongoResult(collection: Collection<T>): Result<Error, List<T>> {
    val mongoCollection = collection.getCollection()
    return try {
        val list = arrayListOf<T>()
	    //TODO change implementation to not use mutable ArrayList.
        mongoCollection.find().consumeEach { list.add(it) }
	    Result.Success(list)
    } catch (e: Exception) {
	    Result.Failure(Error.UnknownError())
    }
}

@PublishedApi
internal suspend fun <T: Any> updateOneSuspendMongoResult(collection: Collection<T>, filter: Bson, entity: T): Result<Error, T> {
    val mongoCollection = collection.getCollection()
    return try {
        val publisher = mongoCollection.findOneAndReplace(filter, entity)
        val resultEntity = publisher.awaitFirstOrNull()
        when {
            resultEntity != null -> Result.Success(resultEntity)
            else -> Result.Failure(Error.EntityNotFound())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@PublishedApi
internal suspend fun <T: Any> deleteOneSuspendMongoResult(collection: Collection<T>, filter: Bson): Result<Error, T> {
    val mongoCollection = collection.getCollection()
    return try {
        val publisher = mongoCollection.findOneAndDelete(filter)
        val resultEntity = publisher.awaitFirstOrNull()
        when {
            resultEntity != null -> Result.Success(resultEntity)
            else -> Result.Failure(Error.EntityNotFound())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@PublishedApi
internal suspend fun <T: Any> deleteAllSuspendMongoResult(collection: Collection<T>): Result<Error, Boolean> {
    val mongoCollection = collection.getCollection()
    return try {
        val publisher = mongoCollection.drop()
        val success = publisher.awaitFirstOrNull()
        when (success) {
            SUCCESS -> Result.Success(true)
            else -> Result.Failure(Error.EntityNotFound())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}
