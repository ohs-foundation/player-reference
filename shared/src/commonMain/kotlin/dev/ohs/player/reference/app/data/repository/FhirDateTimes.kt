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
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant

/**
 * The point in time a FHIR dateTime denotes, for ordering. A full date-time honours its UTC offset;
 * a partial one (year, year-month or date) counts from its start in UTC.
 */
internal fun FhirDateTime.toInstant(): Instant =
  when (this) {
    is FhirDateTime.Year -> LocalDate(value, 1, 1).atStartOfDayIn(TimeZone.UTC)
    is FhirDateTime.YearMonth -> LocalDate(value.year, value.month, 1).atStartOfDayIn(TimeZone.UTC)
    is FhirDateTime.Date -> date.atStartOfDayIn(TimeZone.UTC)
    is FhirDateTime.DateTime -> dateTime.toInstant(utcOffset)
  }
