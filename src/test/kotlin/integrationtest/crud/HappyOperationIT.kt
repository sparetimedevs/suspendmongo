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

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.resilience.Resilience
import com.sparetimedevs.suspendmongo.result.fold
import example.model.TestUser
import example.repository.TestUserRepository
import io.kotlintest.TestCase
import io.kotlintest.extensions.TestListener
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.bson.conversions.Bson
import test.listener.StartTestContainersListener

class HappyOperationIT : BehaviorSpec() {

	override fun listeners(): List<TestListener> = listOf(StartTestContainersListener)

	private var testUserRepository: TestUserRepository? = null

	override fun beforeTest(testCase: TestCase) {
		super.beforeTest(testCase)
		testUserRepository = InstanceTestUserRepository().testUserRepository
	}

	init {

		given("a document in the MongoDB") {
			`when`("read one by id") {
				then("retrieves that document.") {
					val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")

					testUserRepository!!.createOne(testUser)

					val resultContainingTestUser = testUserRepository!!.readOne(testUser.id)

					resultContainingTestUser.fold(
							{ fail("This test case should yield a Success, but was: $it") },
							{ it shouldBe testUser }
					)
				}
			}

			`when`("read one by first name and last name") {
				then("retrieves that document.") {
					val testUser = TestUser(firstName = "d", lastName = "e", age = 29, email = "f")

					testUserRepository!!.createOne(testUser)

					val resultContainingTestUser = testUserRepository!!.readOne(
							"firstName" to testUser.firstName,
							"lastName" to testUser.lastName
					)

					resultContainingTestUser.fold(
							{ fail("This test case should yield a Success, but was: $it") },
							{ it shouldBe testUser }
					)
				}
			}

			`when`("read one by wrong last name or right age") {
				then("retrieves that document.") {
					val testUser = TestUser(firstName = "d", lastName = "e", age = 37, email = "f")

					testUserRepository!!.createOne(testUser)

					val lastNameFilter = Filters.eq("lastName", "Wrong")
					val ageFilter = Filters.eq("age", 37)
					val filter: Bson = Filters.or(listOf(lastNameFilter, ageFilter))

					val resultContainingTestUser = testUserRepository!!.readOne(filter)

					resultContainingTestUser.fold(
							{ fail("This test case should yield a Success, but was: $it") },
							{ it shouldBe testUser }
					)
				}
			}
		}

		given("four documents in the MongoDB") {
			`when`("count all") {
				then("counts all documents.") {
					testUserRepository!!.deleteAll()

					val testUser1 = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")
					val testUser2 = TestUser(firstName = "d", lastName = "e", age = 29, email = "f")
					val testUser3 = TestUser(firstName = "g", lastName = "h", age = 27, email = "i")
					val testUser4 = TestUser(firstName = "j", lastName = "k", age = 25, email = "l")

					testUserRepository!!.createOne(testUser1)
					testUserRepository!!.createOne(testUser2)
					testUserRepository!!.createOne(testUser3)
					testUserRepository!!.createOne(testUser4)

					val count = 4L

					val resultContainingCount = testUserRepository!!.countAll()

					resultContainingCount.fold(
							{ fail("This test case should yield a Success, but was: $it") },
							{ it shouldBe count }
					)
				}
			}
		}

		given("five documents in the MongoDB") {
			`when`("read all sorted") {
				then("reads all documents sorted.") {
					testUserRepository!!.deleteAll()

					val testUser1 = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")
					val testUser2 = TestUser(firstName = "d", lastName = "e", age = 29, email = "f")
					val testUser3 = TestUser(firstName = "g", lastName = "h", age = 27, email = "i")
					val testUser4 = TestUser(firstName = "j", lastName = "k", age = 25, email = "l")
					val testUser5 = TestUser(firstName = "m", lastName = "n", age = 30, email = "0")

					testUserRepository!!.createOne(testUser1)
					testUserRepository!!.createOne(testUser2)
					testUserRepository!!.createOne(testUser3)
					testUserRepository!!.createOne(testUser4)
					testUserRepository!!.createOne(testUser5)

					val sort: Bson = Sorts.descending("age")

					val resultContainingReadAll = testUserRepository!!.readAll(sort)

					val listOfTestUsersSortedByAgeDescending = arrayListOf(
							testUser1,
							testUser5,
							testUser2,
							testUser3,
							testUser4
					)

					resultContainingReadAll.fold(
							{ fail("This test case should yield a Success, but was: $it") },
							{ it shouldBe listOfTestUsersSortedByAgeDescending }
					)
				}
			}
		}
	}

	private class InstanceTestUserRepository {
		internal val testUserRepository: TestUserRepository
		init {
			val resilience = Resilience(30, 20, 1)
			val ipAddressViaToxiproxy: String? = StartTestContainersListener.getMongoDbProxy().containerIpAddress
			val portViaToxiproxy: Int? = StartTestContainersListener.getMongoDbProxy().proxyPort
			val connectionString = "mongodb://$ipAddressViaToxiproxy:$portViaToxiproxy"
			val eventLoopGroup: EventLoopGroup = NioEventLoopGroup()
			val database = Database(connectionString, "happy-test-database", resilience, eventLoopGroup)
			testUserRepository = TestUserRepository(database)
		}
	}
}
