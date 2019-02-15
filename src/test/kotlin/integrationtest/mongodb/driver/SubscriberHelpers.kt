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


/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package integrationtest.mongodb.driver

import com.mongodb.MongoTimeoutException
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.ArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Implementation derived from the QuickTour code example provided by MongoDB, Inc.
 * The QuickTour code example: https://mongodb.github.io/mongo-java-driver-reactivestreams/1.0/getting-started
 */
internal object SubscriberHelpers {

	/**
	 * A Subscriber that stores the publishers results and provides a latch so can block on completion.
	 *
	 * @param <T> The publishers result type </T>
	 */
	open class ObservableSubscriber<T> internal constructor() : Subscriber<T> {
		private val received: MutableList<T>
		private val errors: MutableList<Throwable>
		private val latch: CountDownLatch
		@Volatile
		var subscription: Subscription? = null
			private set
		@Volatile
		var isCompleted: Boolean = false
			private set
		val error: Throwable?
			get() = if (errors.size > 0) {
				errors.get(0)
			} else null

		init {
			this.received = ArrayList()
			this.errors = ArrayList()
			this.latch = CountDownLatch(1)
		}

		override fun onSubscribe(s: Subscription) {
			subscription = s
		}

		override fun onNext(t: T) {
			received.add(t)
		}

		override fun onError(t: Throwable) {
			errors.add(t)
			onComplete()
		}

		override fun onComplete() {
			isCompleted = true
			latch.countDown()
		}

		fun getReceived(): List<T> {
			return received
		}

		@Throws(Throwable::class)
		operator fun get(timeout: Long, unit: TimeUnit): List<T> {
			return await(timeout, unit).getReceived()
		}

		@Throws(Throwable::class)
		@JvmOverloads
		fun await(
				timeout: Long = java.lang.Long.MAX_VALUE,
				unit: TimeUnit = TimeUnit.MILLISECONDS
		): ObservableSubscriber<T> {
			subscription!!.request(Integer.MAX_VALUE.toLong())
			if (!latch.await(timeout, unit)) {
				throw MongoTimeoutException("Publisher onComplete timed out")
			}
			if (!errors.isEmpty()) {
				throw errors[0]
			}
			return this
		}
	}

	/**
	 * A Subscriber that immediately requests Integer.MAX_VALUE onSubscribe
	 *
	 * @param <T> The publishers result type </T>
	 */
	open class OperationSubscriber<T> : ObservableSubscriber<T>() {

		override fun onSubscribe(s: Subscription) {
			super.onSubscribe(s)
			s.request(Integer.MAX_VALUE.toLong())
		}
	}
}
