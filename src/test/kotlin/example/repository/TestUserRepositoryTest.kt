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

import com.mongodb.client.model.Sorts
import com.sparetimedevs.suspendmongo.Collection
import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.crud.countAllSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.createOneSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.deleteAllSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.deleteOneSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.readAllSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.readOneSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.updateOneSuspendMongoResult
import com.sparetimedevs.suspendmongo.result.Result
import com.sparetimedevs.suspendmongo.result.fold
import example.model.TestUser
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bson.conversions.Bson

class TestUserRepositoryTest : BehaviorSpec({

	given("a test user in the test user collection") {
		`when`("read one by id") {
			then("returns that test user.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")

				coEvery { readOneSuspendMongoResult(any<Collection<TestUser>>(), any()) } returns Result.Success(testUser)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingTestUser = userRepository.readOne(testUser.id)

				resultContainingTestUser.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe testUser }
				)
			}
		}

		`when`("delete one") {
			then("deletes that test user.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")

				coEvery { deleteOneSuspendMongoResult(any<Collection<TestUser>>(), any()) } returns Result.Success(testUser)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingTestUser = userRepository.deleteOne(testUser.id)

				resultContainingTestUser.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe testUser }
				)
			}
		}

		`when`("update one") {
			then("updates that test user.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")

				coEvery { updateOneSuspendMongoResult(any<Collection<TestUser>>(), any(), any()) } returns Result.Success(testUser)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingTestUser = userRepository.updateOne(testUser.id, testUser)

				resultContainingTestUser.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe testUser }
				)
			}
		}
	}

	given("multiple test users in the test user collection") {
		`when`("read all") {
			then("returns all test users.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val listOfTestUsersInMongo = arrayListOf(
						TestUser(firstName = "a", lastName = "b", age = 31, email = "c"),
						TestUser(firstName = "d", lastName = "e", age = 29, email = "f")
				)

				coEvery { readAllSuspendMongoResult(any<Collection<TestUser>>()) } returns Result.Success(listOfTestUsersInMongo)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingListOfTestUsers = userRepository.readAll()

				resultContainingListOfTestUsers.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe listOfTestUsersInMongo }
				)
			}
		}

		`when`("read all sorted") {
			then("returns all test users sorted.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val listOfTestUsersInMongo = arrayListOf(
						TestUser(firstName = "a", lastName = "b", age = 31, email = "c"),
						TestUser(firstName = "d", lastName = "e", age = 29, email = "f")
				)

				val sort: Bson = Sorts.descending("firstName")

				coEvery { readAllSuspendMongoResult(any<Collection<TestUser>>(), sort) } returns Result.Success(listOfTestUsersInMongo)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingListOfTestUsers = userRepository.readAll(sort)

				resultContainingListOfTestUsers.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe listOfTestUsersInMongo }
				)
			}
		}

		`when`("delete all") {
			then("deletes all test users.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				coEvery { deleteAllSuspendMongoResult(any<Collection<TestUser>>()) } returns Result.Success(true)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingTrue = userRepository.deleteAll()

				resultContainingTrue.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe true }
				)
			}
		}

		`when`("count all") {
			then("counts all test users.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val count = 444L

				coEvery { countAllSuspendMongoResult(any<Collection<TestUser>>()) } returns Result.Success(count)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingCount = userRepository.countAll()

				resultContainingCount.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe count }
				)
			}
		}
	}

	given("a test user collection") {
		`when`("create one") {
			then("creates a test user in the test user collection.") {
				val mockDatabase = mockk<Database>()
				mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

				val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")

				coEvery { createOneSuspendMongoResult(any<Collection<TestUser>>(), any()) } returns Result.Success(testUser)

				val userRepository = TestUserRepository(mockDatabase)

				val resultContainingTestUser = userRepository.createOne(testUser)

				resultContainingTestUser.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe testUser }
				)
			}
		}
	}
})
