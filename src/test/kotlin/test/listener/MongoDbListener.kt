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

package test.listener

import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import io.kotlintest.extensions.TestListener

private const val bindIp = "localhost"
private const val port = 12345
const val connectionString = "mongodb://$bindIp:$port"

abstract class MongoDbListener : TestListener {

	private val mongodConfig = MongodConfigBuilder()
			.version(Version.Main.PRODUCTION)
			.net(Net(bindIp, port, Network.localhostIsIPv6()))
			.build()
	private val starter = MongodStarter.getDefaultInstance()
	private val mongodExecutable = starter.prepare(mongodConfig)
	internal var mongodProcess: MongodProcess? = null

	fun startMongoDb() {
		mongodProcess = mongodExecutable.start()
	}

	fun stopMongoDb() {
		mongodExecutable.stop()
	}
}
