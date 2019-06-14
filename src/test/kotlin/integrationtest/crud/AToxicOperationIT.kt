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
import eu.rekawek.toxiproxy.model.ToxicDirection
import example.model.TestUser
import example.repository.TestUserRepository
import io.kotlintest.TestCase
import io.kotlintest.extensions.TestListener
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import test.listener.StartTestContainersListener

/**
 * This class is called AToxicOperationIT instead of ToxicOperationIT because the order of test execution seems to matter.
 * When called ToxicOperationIT and all tests are run, the tests in this class fail because of reasons yet to determine.
 **/
class AToxicOperationIT : BehaviorSpec() {

	override fun listeners(): List<TestListener> = listOf(StartTestContainersListener)

	private var testUserRepository: TestUserRepository? = null

	override fun beforeTest(testCase: TestCase) {
		super.beforeTest(testCase)
		testUserRepository = InstanceToxicTestUserRepository().testUserRepository
	}

	init {

		given("another document in the MongoDB") {
			and("a network connection latency") {
				`when`("read one by id") {
					then("retrieves that document.") {
						val testUser = TestUser(firstName = "Ze", lastName = "Name", age = 23, email = "test@mail.com")

						testUserRepository!!.createOne(testUser)

						GlobalScope.launch {
							StartTestContainersListener.getMongoDbProxy().toxics()
									.latency("latency", ToxicDirection.UPSTREAM, TOXIC_TIME)
							delay(TOXIC_TIME_PLUS_OFFSET)
							StartTestContainersListener.getMongoDbProxy().toxics()["latency"].remove()
						}

						val resultContainingTestUser = testUserRepository!!.readOne(testUser.id)

						resultContainingTestUser.fold(
								{ fail("This test case should yield a Success, but was: $it") },
								{ it shouldBe testUser }
						)
					}
				}
			}

			and("a network connection timeout") {
				`when`("read one by id") {
					then("retrieves that document.") {
						val testUser = TestUser(firstName = "Another", lastName = "User", age = 24, email = "another@mail.com")

						testUserRepository!!.createOne(testUser)

						GlobalScope.launch {
							StartTestContainersListener.getMongoDbProxy().toxics()
									.timeout("timeout", ToxicDirection.UPSTREAM, TOXIC_TIME)
							delay(TOXIC_TIME_PLUS_OFFSET)
							StartTestContainersListener.getMongoDbProxy().toxics()["timeout"].remove()
						}

						val resultContainingTestUser = testUserRepository!!.readOne(testUser.id)

						resultContainingTestUser.fold(
								{ fail("This test case should yield a Success, but was: $it") },
								{ it shouldBe testUser }
						)
					}
				}
			}
		}
	}

	companion object {
		private const val TOXIC_TIME: Long = 10000L
		private const val TOXIC_TIME_PLUS_OFFSET: Long = TOXIC_TIME + 50L
	}

	private class InstanceToxicTestUserRepository {
		internal val testUserRepository: TestUserRepository
		init {
			val resilience = Resilience(15, 5000, 5)
			val ipAddressViaToxiproxy: String? = StartTestContainersListener.getMongoDbProxy().containerIpAddress
			val portViaToxiproxy: Int? = StartTestContainersListener.getMongoDbProxy().proxyPort
			val connectionString = "mongodb://$ipAddressViaToxiproxy:$portViaToxiproxy"
			val eventLoopGroup: EventLoopGroup = NioEventLoopGroup()
			val database = Database(connectionString, "toxic-test-database", resilience, eventLoopGroup)
			testUserRepository = TestUserRepository(database)
		}
	}
}
