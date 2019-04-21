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

package com.sparetimedevs.suspendmongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import com.sparetimedevs.suspendmongo.resilience.Resilience
import io.netty.channel.EventLoopGroup
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

/**
 * When using an EventLoopGroup, be sure to shut it down (gracefully).
 **/
class Database(connectionString: String, name: String, internal val resilience: Resilience = Resilience(), eventLoopGroup: EventLoopGroup? = null) {

	internal val mongoDatabase: MongoDatabase

	init {
		val pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
		val settings: MongoClientSettings = when (eventLoopGroup) {
			null -> {
				MongoClientSettings.builder()
						.applyConnectionString(ConnectionString(connectionString))
						.codecRegistry(pojoCodecRegistry)
						.build()
			}
			else -> {
				MongoClientSettings.builder()
						.applyConnectionString(ConnectionString(connectionString))
						.codecRegistry(pojoCodecRegistry)
						.streamFactoryFactory(
								NettyStreamFactoryFactory
										.builder()
										.eventLoopGroup(eventLoopGroup)
										.build()
						)
						.build()
			}
		}
		val client = MongoClients.create(settings)
		mongoDatabase = client.getDatabase(name)
	}
}
