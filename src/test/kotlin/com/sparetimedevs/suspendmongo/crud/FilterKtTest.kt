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

import com.sparetimedevs.suspendmongo.TestObject
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.specs.BehaviorSpec
import org.bson.types.ObjectId

class FilterKtTest : BehaviorSpec({

	given("an id") {
		`when`("id query filter") {
			then("returns query filter with that id.") {
				val id = ObjectId()
				val filter = idQueryFilter(id)
				filter.toString() shouldContain "_id"
				filter.toString() shouldContain id.toHexString()
			}
		}
	}

	given("a pair of field name and value") {
		`when`("equals query filter") {
			then("returns query filter for that pair.") {
				val fieldName = "testFieldName"
				val value = TestObject(a = "a", b = 2)
				val filter = equalsQueryFilter(fieldName to value)
				filter.toString() shouldContain "testFieldName"
				filter.toString() shouldContain value.toString()
			}
		}
	}
})
