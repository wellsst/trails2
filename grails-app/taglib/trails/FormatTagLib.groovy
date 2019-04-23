package trails

import utils.FormatUtils

/**
 * Created by Ariba
 * User: i079413
 * Date: 4/05/16
 * Time: 2:57 PM
 */
class FormatTagLib {

    /**
     * Formats a duration in HHMMSS.
     *
     * @attr formats a long number represting seconds of a durection to HH:MM:SS
     */
    def formatDuration = { attrs, body ->
        out << FormatUtils.secondsToHHMMSS(attrs.duration)
    }

    /**
     * Formats a duration in HHMMSS.
     *
     * @attr formats a long number represting seconds of a durection to HH:MM:SS
     */
    def formatSpeedAsPace = { attrs, body ->

        BigDecimal speed = new BigDecimal(attrs.speed)

        if (speed == 0) {
            speed = 60
        }
        BigDecimal pace = 60 / speed

        String[] split = pace.toString().split("\\.");

        BigDecimal decimal = new BigDecimal(split[0]);
        BigDecimal fraction = pace.subtract(decimal);

        BigDecimal secs = new BigDecimal(fraction *60)

        out << "${decimal}:${secs.toString().split("\\.")[0].padLeft(2, '0')}"
    }
}
