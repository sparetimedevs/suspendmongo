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

package integrationtest.mongodb

import integrationtest.mongodb.driver.MongodbDriver
import integrationtest.mongodb.driver.ReactiveStreamsMongodbDriver
import integrationtest.mongodb.driver.SyncMongodbDriver
import io.kotlintest.IsolationMode
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.numerics.shouldBeLessThan
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.bson.Document
import test.listener.StartMongoDbListener
import test.listener.connectionString

class MongodbDriverComparisonIT : BehaviorSpec() {

	override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
	override fun listeners(): List<TestListener> = listOf(StartMongoDbListener)

	init {

		given("five inserts at one moment") {
			`when`("testing mongodb reactive streams driver and mongodb sync driver") {
				then("reactive streams driver is faster.") {
					val reactiveStreamsMongodbDriver = ReactiveStreamsMongodbDriver(connectionString, "test-database-1")
					val syncMongodbDriver = SyncMongodbDriver(connectionString, "test-database-2")

					val executionTimeUsingReactiveStreamsDriver = executionTime(FIVE_DOCS, reactiveStreamsMongodbDriver::insert)
					val executionTimeUsingSyncDriver = executionTime(FIVE_DOCS, syncMongodbDriver::insert)

					println("execution time using reactive streams driver = $executionTimeUsingReactiveStreamsDriver")
					println("execution time using sync driver = $executionTimeUsingSyncDriver")

					insertedDocsShouldBeAsExpected(reactiveStreamsMongodbDriver, FIVE_DOCS)
					insertedDocsShouldBeAsExpected(syncMongodbDriver, FIVE_DOCS)

					executionTimeUsingReactiveStreamsDriver shouldBeLessThan executionTimeUsingSyncDriver
				}
			}
		}

		given("two hundred and fifty inserts at one moment") {
			`when`("testing mongodb reactive streams driver and mongodb sync driver") {
				then("reactive streams driver is faster.") {

					val reactiveStreamsMongodbDriver = ReactiveStreamsMongodbDriver(connectionString, "test-database-3")
					val syncMongodbDriver = SyncMongodbDriver(connectionString, "test-database-4")

					val executionTimeUsingReactiveStreamsDriver = executionTime(TWO_HUNDRED_AND_FIFTY_DOCS, reactiveStreamsMongodbDriver::insert)
					val executionTimeUsingSyncDriver = executionTime(TWO_HUNDRED_AND_FIFTY_DOCS, syncMongodbDriver::insert)

					println("execution time using reactive streams driver = $executionTimeUsingReactiveStreamsDriver")
					println("execution time using sync driver = $executionTimeUsingSyncDriver")

					insertedDocsShouldBeAsExpected(reactiveStreamsMongodbDriver, TWO_HUNDRED_AND_FIFTY_DOCS)
					insertedDocsShouldBeAsExpected(syncMongodbDriver, TWO_HUNDRED_AND_FIFTY_DOCS)

					executionTimeUsingReactiveStreamsDriver shouldBeLessThan executionTimeUsingSyncDriver
				}
			}
		}
	}

	private fun executionTime(docsToInsert: List<Document>, insertFunction: (docsToInsert: List<Document>) -> Unit): Long {
		val startTime = System.currentTimeMillis()
		insertFunction(docsToInsert)
		val endTime = System.currentTimeMillis()
		return endTime - startTime
	}

	private fun insertedDocsShouldBeAsExpected(mongodbDriver: MongodbDriver, expectedList: List<Document>) {
		val docs = mongodbDriver.find(expectedList.size)

		docs.size shouldBe expectedList.size
		docs shouldContainAll expectedList
	}
}
