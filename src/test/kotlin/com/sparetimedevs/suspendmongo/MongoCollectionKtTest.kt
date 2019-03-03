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
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId

internal class MongoCollectionKtTest : StringSpec({

	"Given ... when ... then ..." {
		val mockDatabase = mockk<Database>()

		val collection = getCollection<TestType>(mockDatabase)

		(collection is Collection<TestType>) shouldBe true
	}

	"Given ... when ... then ... something else" {
		val collectionName = TestType::class.java.simpleName!!
		val clazz = TestType::class.java

		val mockDatabase = mockk<Database>()
		val mockMongoDatabase = mockk<MongoDatabase>()
		val mockCollection = mockk<MongoCollection<TestType>>()

		every { mockDatabase.mongoDatabase } returns mockMongoDatabase
		every { mockMongoDatabase.getCollection(collectionName, clazz) } returns mockCollection

		val collection = Collection(mockDatabase, collectionName, clazz).getCollection()

		collection shouldBe mockCollection
	}

})

data class TestType(val id: ObjectId = ObjectId(), val a: String, val b: Int)
