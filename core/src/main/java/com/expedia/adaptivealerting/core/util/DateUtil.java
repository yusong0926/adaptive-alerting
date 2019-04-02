/*
 * Copyright 2018-2019 Expedia Group, Inc.
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
package com.expedia.adaptivealerting.core.util;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static com.expedia.adaptivealerting.core.util.AssertUtil.notNull;

/**
 * Date and time utilities.
 */
public final class DateUtil {

    /**
     * Prevent instantiation.
     */
    private DateUtil() {
    }

    /**
     * Returns the largest day either before or equal to the given date.
     *
     * @param date A date.
     * @return Largest day before or equal to the given date.
     */
    public static Instant truncatedToDay(Instant date) {
        notNull(date, "date can't be null");
        return date.truncatedTo(ChronoUnit.DAYS);
    }

    /**
     * Returns the largest week either before or equal to the given date, based on UTC time.
     *
     * @param date A date.
     * @return Largest week before or equal to the given week.
     */
    public static Instant truncatedToWeek(Instant date) {
        notNull(date, "date can't be null");
        final DayOfWeek dow = ZonedDateTime.ofInstant(date, ZoneOffset.UTC).getDayOfWeek();
        return truncatedToDay(date).minus(Duration.ofDays(dow.getValue() % 7));
    }
}
