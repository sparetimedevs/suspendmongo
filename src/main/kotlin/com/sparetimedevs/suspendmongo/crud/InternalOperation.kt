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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.flow.asFlow
import org.bson.conversions.Bson
import java.util.function.Supplier

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> createOneSuspendMongoResult(collection: Collection<T>, entity: T): Result<Error, T> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        val success = resilience.executeSupplier( Supplier { mongoCollection.insertOne(entity) }).asFlow().single()
        when (success) {
            SUCCESS -> Result.Success(entity)
            else -> Result.Failure(Error.UnknownError())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> readOneSuspendMongoResult(collection: Collection<T>, filter: Bson): Result<Error, T> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        Result.Success(
                resilience.executeSupplier( Supplier { mongoCollection.find(filter) }).asFlow().single()
        )
    } catch (e: NoSuchElementException) {
        Result.Failure(Error.EntityNotFound())
    } catch (e: IllegalStateException) {
        Result.Failure(Error.UnknownError("Expected a single result, but found multiple results."))
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> readAllSuspendMongoResult(collection: Collection<T>): Result<Error, List<T>> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        Result.Success(
                resilience.executeSupplier( Supplier { mongoCollection.find() }).asFlow().toList()
        )
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> updateOneSuspendMongoResult(collection: Collection<T>, filter: Bson, entity: T): Result<Error, T> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        Result.Success(
                resilience.executeSupplier( Supplier { mongoCollection.findOneAndReplace(filter, entity) }).asFlow().single()
        )
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> deleteOneSuspendMongoResult(collection: Collection<T>, filter: Bson): Result<Error, T> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        Result.Success(
                resilience.executeSupplier( Supplier { mongoCollection.findOneAndDelete(filter) }).asFlow().single()
        )
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> deleteAllSuspendMongoResult(collection: Collection<T>): Result<Error, Boolean> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        val success = resilience.executeSupplier( Supplier { mongoCollection.drop() }).asFlow().single()
        when (success) {
            SUCCESS -> Result.Success(true)
            else -> Result.Failure(Error.EntityNotFound())
        }
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}

@FlowPreview
@PublishedApi
internal suspend fun <T: Any> countAllSuspendMongoResult(collection: Collection<T>): Result<Error, Long> {
    val resilience = collection.getDatabaseResilience()
    val mongoCollection = collection.getMongoCollection()
    return try {
        Result.Success(
                resilience.executeSupplier( Supplier { mongoCollection.countDocuments() }).asFlow().single()
        )
    } catch (e: Exception) {
        Result.Failure(Error.UnknownError())
    }
}
