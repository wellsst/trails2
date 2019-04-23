package utils

/**
 * Created by Ariba
 * User: i079413
 * Date: 24/04/16
 * Time: 11:20 AM
 */
class SummaryStatistics {

    Date fromDate

    int totalActivities = 0

    SummaryStatistics(int totalActivities) {
        this.totalActivities = totalActivities
    }

    BigDecimal distance  = 0
    long totalDurationSeconds = 0
    int sufferScore = 0

    int averageHR = 0
    BigDecimal averageSpeed = 0

    String displayText

    static transients = ['displayText']

    BigDecimal getAverageDistance() {
        totalActivities == 0 ? 0 : distance / totalActivities
    }

    String getTotalDurationFormatted() { // Want these times to be in HH:MM:SS
        FormatUtils.secondsToHHMMSS(totalDurationSeconds)
    }

    String getAverageDurationFormatted() {
        totalActivities == 0 ? "00:00:00" : FormatUtils.secondsToHHMMSS(Math.round(totalDurationSeconds / totalActivities))
    }

    String getAveragePaceFormatted() {
        totalActivities == 0 ? "00:00:00" : FormatUtils.formatSpeedAsPace(averageSpeed)
    }

    int getAverageSufferScore() {
        totalActivities == 0 ? 0 : sufferScore / totalActivities
    }
}
