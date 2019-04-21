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

package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.extensions.TestListener
import test.listener.StopTestContainersListener
import java.lang.System.currentTimeMillis

/**
 * Project-wide configuration.
 * KotlinTest will detect its presence and use it when executing tests.
 */
object ProjectConfig : AbstractProjectConfig() {

	override fun listeners(): List<TestListener> = listOf(StopTestContainersListener)

	private var started: Long = 0

	override fun beforeAll() {
		started = currentTimeMillis()
	}

	override fun afterAll() {
		val timeInMilliseconds = currentTimeMillis() - started
		val timeInSeconds = (timeInMilliseconds / 1000) % 60
		println("overall time in seconds: $timeInSeconds")
	}
}
