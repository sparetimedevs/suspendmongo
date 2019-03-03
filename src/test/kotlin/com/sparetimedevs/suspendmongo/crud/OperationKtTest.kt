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

import com.sparetimedevs.suspendmongo.Collection
import com.sparetimedevs.suspendmongo.TestType
import com.sparetimedevs.suspendmongo.result.Result
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking

internal class OperationKtTest : StringSpec({

	"read all" {
		runBlocking {
			val mockCollection = mockk<Collection<TestType>>()
			mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

			val listOfTestTypesInMongo = arrayListOf(
					TestType(a = "a", b = 2),
					TestType(a = "d", b = 4)
			)
			val returnedSuspendMongoResult = Result.Success(listOfTestTypesInMongo)

			coEvery { readAllSuspendMongoResult(any<Collection<TestType>>()) } returns returnedSuspendMongoResult

			val listOfTestTypesInSuspendMongoResult = mockCollection.readAll()

			listOfTestTypesInSuspendMongoResult shouldBe returnedSuspendMongoResult
		}
	}

	"read one" {
		runBlocking {
			val mockCollection = mockk<Collection<TestType>>()
			mockkStatic("com.sparetimedevs.suspendmongo.crud.InternalOperationKt")

			val testType = TestType(a = "a", b = 2)
			val returnedSuspendMongoResult = Result.Success(testType)

			coEvery { readOneSuspendMongoResult(any<Collection<TestType>>(), any()) } returns returnedSuspendMongoResult

			val testTypeInSuspendMongoResult = mockCollection.readOne(testType.id)

			testTypeInSuspendMongoResult shouldBe returnedSuspendMongoResult
		}
	}
})
