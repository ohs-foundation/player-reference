/*
 * Copyright 2026 Open Health Stack Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ohs.player.reference.app.data.repository

import dev.ohs.fhir.model.r4.FhirDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class FhirDateTimesTest {

  private fun dateTime(text: String): FhirDateTime = checkNotNull(FhirDateTime.fromString(text))

  @Test
  fun ordersByTheInstantNotTheText() {
    // 23:00 in Honolulu is 09:00 UTC the next day, later than 01:00 in Cairo (23:00 UTC) —
    // even though "2026-03-02T01:00:00+02:00" sorts after "2026-03-01T23:00:00-10:00" as text.
    val honolulu = dateTime("2026-03-01T23:00:00-10:00")
    val cairo = dateTime("2026-03-02T01:00:00+02:00")
    assertEquals(listOf(cairo, honolulu), listOf(honolulu, cairo).sortedBy { it.toInstant() })
  }

  @Test
  fun placesPartialDatesAtTheirStart() {
    val year = dateTime("2026")
    val month = dateTime("2026-03")
    val day = dateTime("2026-03-15")
    val time = dateTime("2026-03-15T00:00:01Z")
    assertEquals(
      listOf(year, month, day, time),
      listOf(time, day, month, year).sortedBy { it.toInstant() },
    )
  }

  @Test
  fun descendingWithNullsPutsUndatedLast() {
    val dated = dateTime("2026-03-15")
    val sorted =
      listOf<FhirDateTime?>(null, dated).sortedWith(compareByDescending { it?.toInstant() })
    assertEquals(listOf(dated, null), sorted)
  }
}
