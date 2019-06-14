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

import io.kotlintest.extensions.TestListener
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.Network.newNetwork
import org.testcontainers.containers.ToxiproxyContainer
import org.testcontainers.containers.ToxiproxyContainer.ContainerProxy

abstract class TestContainersListener : TestListener {

	internal fun startTestContainers() {
		mongoDbContainer.start()
		toxiproxyContainer.start()
	}

	internal fun stopTestContainers() {
		toxiproxyContainer.stop()
		mongoDbContainer.stop()
	}

	internal fun getMongoDbProxy(): ContainerProxy = toxiproxyContainer.getProxy(mongoDbContainer, MONGO_DB_PORT)

	companion object {
		private const val MONGO_DB_PORT = 27017
		private val network: Network = newNetwork()

		private val mongoDbContainer: GenericContainer<KGenericContainer> = KGenericContainer("mongo:4.1.3")
				.withExposedPorts(MONGO_DB_PORT)
				.withNetwork(network)

		private val toxiproxyContainer: ToxiproxyContainer = ToxiproxyContainer()
				.withNetwork(network)
	}
}

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)
