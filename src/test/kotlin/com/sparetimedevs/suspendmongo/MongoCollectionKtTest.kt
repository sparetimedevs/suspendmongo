package com.sparetimedevs.suspendmongo

import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

internal class MongoCollectionKtTest : StringSpec({

	"Given ... when ... then ..." {
		val mockDatabase = mockk<Database>()
		val mockMongoDatabase = mockk<MongoDatabase>()
		val mockCollection = mockk<MongoCollection<Any>>()

		every { mockDatabase.mongoDatabase } returns mockMongoDatabase
		every { mockMongoDatabase.getCollection(any(), any<Class<Any>>()) } returns mockCollection

		val collection = getCollection<TestType>(mockDatabase)

		collection shouldBe mockCollection
	}

	"Given ... when ... then ... something else" {
	}

})

data class TestType(val a: String, val b: Int)
