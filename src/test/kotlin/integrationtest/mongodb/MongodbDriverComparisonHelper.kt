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

package integrationtest.mongodb

import org.bson.Document

private const val FIVE = 5
internal val FIVE_DOCS = createListOfDocs(FIVE)

private const val TWO_HUNDRED_AND_FIFTY = 250
internal val TWO_HUNDRED_AND_FIFTY_DOCS = createListOfDocs(TWO_HUNDRED_AND_FIFTY)

private fun createListOfDocs(amount: Int) : List<Document> {
	val docsToInsert = mutableListOf<Document>()
	for (i in 1..amount) {
		// make a document and insert it
		val doc = Document("name", "MongoDB$i")
				.append("type", "database$i")
				.append("count", i)
				.append("info", Document("x", 2030 + i).append("y", 1020 + i))

		docsToInsert.add(doc)
	}
	return docsToInsert
}
