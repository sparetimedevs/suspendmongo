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

package integrationtest.crud

import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.resilience.Resilience
import com.sparetimedevs.suspendmongo.result.fold
import example.model.TestUser
import example.repository.TestUserRepository
import io.kotlintest.IsolationMode
import io.kotlintest.extensions.TestListener
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import test.listener.StartMongoDbListener
import test.listener.connectionString

class OperationIT : BehaviorSpec() {

	override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
	override fun listeners(): List<TestListener> = listOf(StartMongoDbListener)

	init {
		val resilience = Resilience(30, 500, 1)
		val database = Database(connectionString, "test-database", resilience)
		val testUserRepository = TestUserRepository(database)

		given("a document in the MongoDB") {
			`when`("read one by id") {
				then("retrieves that document.") {
					val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")

					testUserRepository.createOne(testUser)

					val resultContainingTestUser = testUserRepository.readOne(testUser.id)

					resultContainingTestUser.fold(
							{ fail("This test case should yield a Success.") },
							{ it shouldBe testUser }
					)
				}
			}

			`when`("read one by first name and last name") {
				then("retrieves that document.") {
					val testUser = TestUser(firstName = "d", lastName = "e", age = 29, email = "f")

					testUserRepository.createOne(testUser)

					val resultContainingTestUser = testUserRepository.readOne(
							"firstName" to testUser.firstName,
							"lastName" to testUser.lastName
					)

					resultContainingTestUser.fold(
							{ fail("This test case should yield a Success.") },
							{ it shouldBe testUser }
					)
				}
			}
		}

		given("four documents in the MongoDB") {
			`when`("count all") {
				then("counts all documents.") {
					testUserRepository.deleteAll()

					val testUser1 = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")
					val testUser2 = TestUser(firstName = "d", lastName = "e", age = 29, email = "f")
					val testUser3 = TestUser(firstName = "g", lastName = "h", age = 27, email = "i")
					val testUser4 = TestUser(firstName = "j", lastName = "k", age = 25, email = "l")


					testUserRepository.createOne(testUser1)
					testUserRepository.createOne(testUser2)
					testUserRepository.createOne(testUser3)
					testUserRepository.createOne(testUser4)

					val count = 4L

					val resultContainingCount = testUserRepository.countAll()

					resultContainingCount.fold(
							{ fail("This test case should yield a Success.") },
							{ it shouldBe count }
					)
				}
			}
		}
	}
}
