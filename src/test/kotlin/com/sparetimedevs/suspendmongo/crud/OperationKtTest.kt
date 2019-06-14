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

import com.mongodb.client.model.Sorts
import com.sparetimedevs.suspendmongo.Collection
import com.sparetimedevs.suspendmongo.TestObject
import com.sparetimedevs.suspendmongo.result.Result
import com.sparetimedevs.suspendmongo.result.fold
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bson.conversions.Bson

class OperationKtTest : BehaviorSpec({

	val mockCollection = mockk<Collection<TestObject>>()
	mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

	given("an object in the collection") {
		`when`("read one by id") {
			then("returns that object.") {
				val testObject = TestObject(a = "a", b = 2)
				val returnedSuspendMongoResult = Result.Success(testObject)

				coEvery { readOneSuspendMongoResult(any<Collection<TestObject>>(), any()) } returns returnedSuspendMongoResult

				val testTypeInResult = mockCollection.readOne(testObject.id)

				testTypeInResult shouldBe returnedSuspendMongoResult
			}
		}

		`when`("read one by pairs of field names and values") {
			then("returns that object.") {
				val testType = TestObject(a = "a", b = 2)
				val returnedSuspendMongoResult = Result.Success(testType)

				coEvery { readOneSuspendMongoResult(any<Collection<TestObject>>(), any()) } returns returnedSuspendMongoResult

				val testTypeInResult = mockCollection.readOne(
						"a" to testType.a,
						"b" to testType.b
				)

				testTypeInResult shouldBe returnedSuspendMongoResult
			}
		}
	}

	given("multiple objects in the collection") {
		`when`("read all") {
			then("returns all objects.") {
				val listOfTestTypesInMongo = arrayListOf(
						TestObject(a = "a", b = 2),
						TestObject(a = "d", b = 4)
				)
				val returnedSuspendMongoResult = Result.Success(listOfTestTypesInMongo)

				coEvery { readAllSuspendMongoResult(any<Collection<TestObject>>()) } returns returnedSuspendMongoResult

				val listOfTestTypesInResult = mockCollection.readAll()

				listOfTestTypesInResult shouldBe returnedSuspendMongoResult
			}
		}

		`when`("read all sorted") {
			then("returns all objects sorted.") {
				val listOfTestTypesInMongo = arrayListOf(
						TestObject(a = "a", b = 2),
						TestObject(a = "d", b = 4)
				)

				val sort: Bson = Sorts.descending("a")

				val returnedSuspendMongoResult = Result.Success(listOfTestTypesInMongo)

				coEvery { readAllSuspendMongoResult(any<Collection<TestObject>>(), sort) } returns returnedSuspendMongoResult

				val listOfTestTypesInResult = mockCollection.readAll(sort)

				listOfTestTypesInResult shouldBe returnedSuspendMongoResult
			}
		}

		`when`("count all") {
			then("returns the amount of all objects in the collection.") {
				val count = 789L

				coEvery { countAllSuspendMongoResult(any<Collection<TestObject>>()) } returns Result.Success(count)

				val countOfTestTypesInResult = mockCollection.countAll()

				countOfTestTypesInResult.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe count }
				)
			}
		}
	}
})
