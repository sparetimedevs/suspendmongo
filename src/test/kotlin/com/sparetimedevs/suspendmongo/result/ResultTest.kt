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

package com.sparetimedevs.suspendmongo.result

import com.sparetimedevs.suspendmongo.result.Result.Failure
import com.sparetimedevs.suspendmongo.result.Result.Success
import io.kotlintest.fail
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class ResultTest : StringSpec({

	"Given a string value when result is success then string value is accessible." {
		val stringValue = "Hello world, I am a string."

		val result: Result<Error, String> = Success(stringValue)

		result.fold(
				{ fail("This test case should yield a Success.") },
				{ it shouldBe stringValue }
		)
	}

	"Given an entity not found error when result is failure then entity not found error is accessible." {
		val errorMessage = "This is a test error message stating that the entity is not found."

		val result: Result<Error, String> = Failure(Error.EntityNotFound(errorMessage))

		result.fold(
				{ it shouldBe Error.EntityNotFound(errorMessage) },
				{ fail("This test case should yield a Failure.") }
		)
	}

	"Given a service unavailable error when result is failure then service unavailable error is accessible." {
		val errorMessage = "This is a test error message stating that the service is unavailable."

		val result: Result<Error, String> = Failure(Error.ServiceUnavailable(errorMessage))

		result.fold(
				{ it shouldBe Error.ServiceUnavailable(errorMessage) },
				{ fail("This test case should yield a Failure.") }
		)
	}

	"Given an unknown error when result is failure then unknown error is accessible." {
		val errorMessage = "This is a test error message stating that the error is unknown."

		val result: Result<Error, String> = Failure(Error.UnknownError(errorMessage))

		result.fold(
				{ it shouldBe Error.UnknownError(errorMessage) },
				{ fail("This test case should yield a Failure.") }
		)
	}
})
