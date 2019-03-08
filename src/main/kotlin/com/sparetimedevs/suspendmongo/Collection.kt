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

package com.sparetimedevs.suspendmongo

import com.mongodb.reactivestreams.client.MongoCollection
import com.sparetimedevs.suspendmongo.resilience.Resilience

inline fun <reified T: Any> getCollection(
		database: Database,
		collectionName: String = T::class.java.simpleName!!
): Collection<T> = Collection(database, collectionName, T::class.java)

class Collection<T: Any>(
		private val database: Database,
		private val collectionName: String,
		private val clazz: Class<T>
) {

	internal fun getDatabaseResilience(): Resilience = database.resilience

	internal fun getMongoCollection(): MongoCollection<T> =
			database.mongoDatabase.getCollection(collectionName, clazz)
}
