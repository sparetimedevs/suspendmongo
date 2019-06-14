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

import com.mongodb.client.model.Filters
import com.sparetimedevs.suspendmongo.Collection
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.suspendmongo.result.Result
import org.bson.conversions.Bson
import org.bson.types.ObjectId

suspend inline fun <reified T : Any> Collection<T>.createOne(entity: T): Result<Error, T> {
    return createOneSuspendMongoResult(collection = this, entity = entity)
}

suspend inline fun <reified T : Any> Collection<T>.readOne(id: ObjectId): Result<Error, T> {
    val filter = idQueryFilter(id)
    return readOneSuspendMongoResult(collection = this, filter = filter)
}

suspend inline fun <reified T : Any> Collection<T>.readOne(vararg pairsOfFieldNamesAndValues: Pair<String, Any>): Result<Error, T> {
    val listOfFilters = arrayListOf<Bson>()
    for (pairOfFieldNameAndValue in pairsOfFieldNamesAndValues) {
        listOfFilters.add(equalsQueryFilter(pairOfFieldNameAndValue))
    }
    val filter = Filters.and(listOfFilters)
    return readOneSuspendMongoResult(collection = this, filter = filter)
}

suspend inline fun <reified T : Any> Collection<T>.readOne(filter: Bson): Result<Error, T> {
    return readOneSuspendMongoResult(collection = this, filter = filter)
}

suspend inline fun <reified T : Any> Collection<T>.readAll(): Result<Error, List<T>> {
    return readAllSuspendMongoResult(collection = this)
}

suspend inline fun <reified T : Any> Collection<T>.readAll(sort: Bson): Result<Error, List<T>> {
    return readAllSuspendMongoResult(collection = this, sort = sort)
}

suspend inline fun <reified T : Any> Collection<T>.updateOne(id: ObjectId, entity: T): Result<Error, T> {
    val filter = idQueryFilter(id)
    return updateOneSuspendMongoResult(collection = this, filter = filter, entity = entity)
}

suspend inline fun <reified T : Any> Collection<T>.deleteOne(id: ObjectId): Result<Error, T> {
    val filter = idQueryFilter(id)
    return deleteOneSuspendMongoResult(collection = this, filter = filter)
}

suspend inline fun <reified T : Any> Collection<T>.deleteAll(): Result<Error, Boolean> {
    return deleteAllSuspendMongoResult(collection = this)
}

suspend inline fun <reified T : Any> Collection<T>.countAll(): Result<Error, Long> {
    return countAllSuspendMongoResult(collection = this)
}
