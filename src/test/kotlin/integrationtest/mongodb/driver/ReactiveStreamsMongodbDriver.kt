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

import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.Success
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bson.Document

internal class ReactiveStreamsMongodbDriver(connectionString: String, databaseName: String) : MongodbDriver {

	private val collection: MongoCollection<Document>

	init {
		val mongoClient = MongoClients.create(connectionString)

		val database = mongoClient.getDatabase(databaseName)

		collection = database.getCollection("test-async")

		val subscriber: SubscriberHelpers.ObservableSubscriber<Success> = SubscriberHelpers.ObservableSubscriber()
		collection.drop().subscribe(subscriber)
		subscriber.await()
	}

	override fun insert(docsToInsert: List<Document>): Unit = runBlocking {
		val jobs = ArrayList<Job>()
		for (doc in docsToInsert) {
			val job = launch {
				collection.insertOne(doc).subscribe(SubscriberHelpers.OperationSubscriber<Success>())
			}
			jobs.add(job)
		}
		jobs.forEach { it.join() }
	}

	override fun find(expectedAmount: Int): List<Document> = runBlocking {
		var docs = listOf<Document>()

		var count = 0
		while (docs.size != expectedAmount) {
			when {
				(count == (expectedAmount * 5)) -> {
					throw Exception("Not the amount the test counts on...")
				}
			}
			delay(10L)
			val subscriber1 = SubscriberHelpers.ObservableSubscriber<Document>()
			collection.find().subscribe(subscriber1)
			subscriber1.await()
			docs = subscriber1.getReceived()
			count++
		}
		return@runBlocking docs
	}
}
