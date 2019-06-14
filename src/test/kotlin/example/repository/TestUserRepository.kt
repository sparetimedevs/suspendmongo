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

package example.repository

import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.crud.countAll
import com.sparetimedevs.suspendmongo.crud.createOne
import com.sparetimedevs.suspendmongo.crud.deleteAll
import com.sparetimedevs.suspendmongo.crud.deleteOne
import com.sparetimedevs.suspendmongo.crud.readAll
import com.sparetimedevs.suspendmongo.crud.readOne
import com.sparetimedevs.suspendmongo.crud.updateOne
import com.sparetimedevs.suspendmongo.getCollection
import com.sparetimedevs.suspendmongo.result.Error
import com.sparetimedevs.suspendmongo.result.Result
import example.model.TestUser
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class TestUserRepository(database: Database) {

	private val collection = getCollection<TestUser>(database)

	suspend fun createOne(testUser: TestUser) = collection.createOne(testUser)

	suspend fun readOne(id: ObjectId) = collection.readOne(id)

	suspend fun readOne(vararg pairsOfFieldNamesAndValues: Pair<String, Any>): Result<Error, TestUser> = collection.readOne(*pairsOfFieldNamesAndValues)

	suspend fun readOne(filter: Bson) = collection.readOne(filter)

	suspend fun readAll() = collection.readAll()

	suspend fun readAll(sort: Bson) = collection.readAll(sort)

	suspend fun updateOne(id: ObjectId, testUser: TestUser) = collection.updateOne(id, testUser)

	suspend fun deleteOne(id: ObjectId) = collection.deleteOne(id)

	suspend fun deleteAll() = collection.deleteAll()

	suspend fun countAll() = collection.countAll()
}
