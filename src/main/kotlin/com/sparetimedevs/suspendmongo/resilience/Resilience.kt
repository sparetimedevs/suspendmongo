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

package com.sparetimedevs.suspendmongo.resilience

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadConfig
import io.github.resilience4j.retry.IntervalFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import java.time.Duration

class Resilience(maxConcurrentCalls: Int = 15, maxWaitMillis: Long = 3000, maxAttempts: Int = 3) {

    private val retryConfig = RetryConfig.custom<RetryConfig>()
            .maxAttempts(maxAttempts)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(125L, 3.0))
            .retryOnException { true }
            .build()

    private val bulkheadConfig = BulkheadConfig.custom()
            .maxConcurrentCalls(maxConcurrentCalls)
            .maxWaitDuration(Duration.ofMillis(maxWaitMillis))
            .build()

    internal val retry = Retry.of("mongo-retry", retryConfig)
    internal val bulkhead = Bulkhead.of("mongo-bulkhead", bulkheadConfig)
}
