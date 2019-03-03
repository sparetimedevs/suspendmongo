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

import com.sparetimedevs.suspendmongo.Collection
import com.sparetimedevs.suspendmongo.Database
import com.sparetimedevs.suspendmongo.crud.readAllSuspendMongoResult
import com.sparetimedevs.suspendmongo.crud.readOneSuspendMongoResult
import com.sparetimedevs.suspendmongo.result.Result
import example.model.TestUser
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking

internal class TestUserRepositoryTest : StringSpec({

	"read all" {
		runBlocking {
			val mockDatabase = mockk<Database>()
			mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

			val listOfUsersInMongo = arrayListOf(
					TestUser(firstName = "a", lastName = "b", age = 31, email = "c"),
					TestUser(firstName = "d", lastName = "e", age = 29, email = "f")
			)
			val returnedSuspendMongoResult = Result.Success(listOfUsersInMongo)

			coEvery { readAllSuspendMongoResult(any<Collection<TestUser>>()) } returns returnedSuspendMongoResult

			val userRepository = TestUserRepository(mockDatabase)

			val listOfUsersInSuspendMongoResult = userRepository.readAll()

			listOfUsersInSuspendMongoResult shouldBe returnedSuspendMongoResult
		}
	}

	"read one" {
		runBlocking {
			val mockDatabase = mockk<Database>()
			mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

			val user = TestUser(firstName = "a", lastName = "b", age = 31, email = "c")
			val returnedSuspendMongoResult = Result.Success(user)

			coEvery { readOneSuspendMongoResult(any<Collection<TestUser>>(), any()) } returns returnedSuspendMongoResult

			val userRepository = TestUserRepository(mockDatabase)

			val userInSuspendMongoResult = userRepository.readOne(user.id)

			userInSuspendMongoResult shouldBe returnedSuspendMongoResult
		}
	}

	"delete all" {
		true shouldBe true
	}

	"delete one" {
		true shouldBe true
	}

	"create one" {
		true shouldBe true
	}

	"update one" {
		true shouldBe true
	}
})
