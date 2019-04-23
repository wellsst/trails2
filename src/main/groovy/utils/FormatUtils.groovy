package utils

import groovy.time.TimeCategory
import groovy.time.TimeDuration

/**
 * Created by Ariba
 * User: i079413
 * Date: 26/04/16
 * Time: 2:26 PM
 */
class FormatUtils {

    // Normalize method to return a new TimeDuration
    static TimeDuration normalize(TimeDuration duration) {
        new TimeDuration((duration.days != 0 ? duration.days * 24 : 0) + duration.hours,
                duration.minutes, duration.seconds, duration.millis)
    }

    static String secondsToHHMMSS(long nrSeconds = 0) {

        Date dateAhead = new Date(nrSeconds*1000)  // Date.parseToStringDate( 'Thu May 23 10:10:10 EDT 2013' )
        Date baseDate = new Date(0) // Date.parseToStringDate( 'Tue May 21 12:14:10 EDT 2013' )

        // Then use the category to subtract the dates, and call normalize
        TimeDuration normalized = use(groovy.time.TimeCategory) {
            normalize(dateAhead - baseDate)
        }

        //"${normalized.days}:${normalized.hours.toString().padLeft(2, '0')}:${normalized.minutes.toString().padLeft(2, '0')}:${normalized.seconds.toString().padLeft(2, '0')}"
        "${normalized.hours.toString().padLeft(2, '0')}:${normalized.minutes.toString().padLeft(2, '0')}:${normalized.seconds.toString().padLeft(2, '0')}"
    }

    static String getMonthFromWeekOfYear(int week) {
        Calendar c = Calendar.getInstance()
        c.set(0, 0, 0, 0, 0)
        Date d = new Date(c.time.time)

        use(TimeCategory) {
            d = d + week.week
        }
        //todo: what replaces this deprecated thing...
        (d.month + 1).toString().padLeft(2, '0')
    }

    static String getDateFieldFromWeekOfYear(int week, String format) {
        Calendar c = Calendar.getInstance()
        c.set(0, 0, 0, 0, 0)
        Date d = new Date(c.time.time)

        use(TimeCategory) {
            d = d + week.week
        }
        d.format("MMM")
    }

    static String formatSpeedAsPace(BigDecimal speed) {
        if (speed == 0) {
            speed = 60
        }
        BigDecimal pace = 60 / speed

        String[] split = pace.toString().split("\\.");

        BigDecimal decimal = new BigDecimal(split[0]);
        BigDecimal fraction = pace.subtract(decimal);

        BigDecimal secs = new BigDecimal(fraction *60)

        "${decimal}:${secs.toString().split("\\.")[0].padLeft(2, '0')}"
    }
}
