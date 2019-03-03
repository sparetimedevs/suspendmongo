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

package integrationtest.mongodb.driver

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.Document

internal class SyncMongodbDriver(connectionString: String, databaseName: String) : MongodbDriver {

	private val collection: MongoCollection<Document>

	init {
		val mongoClient = MongoClients.create(connectionString)

		val database = mongoClient.getDatabase(databaseName)

		collection = database.getCollection("test-sync")

		collection.drop()
	}

	override fun insert(docsToInsert: List<Document>): Unit = runBlocking {
		val jobs = ArrayList<Job>()
		for (doc in docsToInsert) {
			val job = launch {
				collection.insertOne(doc)
			}
			jobs.add(job)
		}
		jobs.forEach { it.join() }
	}

	override fun find(expectedAmount: Int): List<Document> = runBlocking {
		var docs = mutableListOf<Document>()

		var count = 0
		while (docs.size != expectedAmount) {
			when {
				(count == 300) -> {
					throw Exception("Not the amount the test counts on...")
				}
			}
			delay(10L)
			val cursor = collection.find().iterator()
			docs = mutableListOf()
			while (cursor.hasNext()) {
				docs.add(cursor.next())
			}
			count++
		}
		return@runBlocking docs
	}
}
