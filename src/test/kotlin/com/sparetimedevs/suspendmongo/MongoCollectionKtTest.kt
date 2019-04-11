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
import com.mongodb.reactivestreams.client.MongoDatabase
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId

class MongoCollectionKtTest : BehaviorSpec({

	given("a specific type of an object") {
		`when`("get collection") {
			then("returns a collection with that type.") {
				val mockDatabase = mockk<Database>()

				val collection = getCollection<TestObject>(mockDatabase)

				(collection is Collection<TestObject>) shouldBe true
			}
		}

		and("a collection name") {
			`when`("get collection") {
				then("returns a collection with that type.") {
					val collectionName = "a test collection name"
					val mockDatabase = mockk<Database>()

					val collection = getCollection<TestObject>(mockDatabase, collectionName)

					(collection is Collection<TestObject>) shouldBe true
				}
			}
		}
	}

	given("a collection of a specific type of an object") {
		`when`("get mongo collection") {
			then("returns a mongo collection with that type.") {
				val collectionName = TestObject::class.java.simpleName!!
				val clazz = TestObject::class.java

				val mockDatabase = mockk<Database>()
				val mockMongoDatabase = mockk<MongoDatabase>()
				val mockCollection = mockk<MongoCollection<TestObject>>()

				every { mockDatabase.mongoDatabase } returns mockMongoDatabase
				every { mockMongoDatabase.getCollection(collectionName, clazz) } returns mockCollection

				val collection = Collection(mockDatabase, collectionName, clazz).getMongoCollection()

				collection shouldBe mockCollection
			}
		}
	}
})

data class TestObject(val id: ObjectId = ObjectId(), val a: String, val b: Int)
