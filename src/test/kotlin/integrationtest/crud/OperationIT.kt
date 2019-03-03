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
import com.sparetimedevs.suspendmongo.result.fold
import example.model.TestUser
import example.repository.TestUserRepository
import io.kotlintest.extensions.TestListener
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.runBlocking
import test.listener.StartMongoDbListener
import test.listener.connectionString

internal class OperationIT : StringSpec() {

	override fun isInstancePerTest(): Boolean = true
	override fun listeners(): List<TestListener> = listOf(StartMongoDbListener)

	init {

		"Given a document in the MongoDB when read one then retrieves that document." {
			val database = Database(connectionString, "test-database")
			val testUserRepository = TestUserRepository(database)
			val testUser = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")
			runBlocking {
				testUserRepository.createOne(testUser)

				val resultContainingTestUser = testUserRepository.readOne(testUser.id)

				resultContainingTestUser.fold(
						{ fail("This test case should yield a Success.") },
						{ it shouldBe testUser }
				)
			}
		}
	}
}
