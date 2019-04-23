package trails.service

import grails.transaction.Transactional
import groovy.time.TimeCategory
import trails.Activity
import trails.ActivityType
import trails.user.UserProfile
import utils.FormatUtils
import utils.SummaryStatistics

/**
 * Date: 27/04/16
 * Time: 1:09 PM
 */
@Transactional
class StatisticsService {
    
    // TODO: Move to i18n props file and this can be the key
    private static ALL_ACTIVITIES_TEXT = "All"

    private SummaryStatistics summaryStatsFromActivities(List<Activity> activities, String displayText) {
        SummaryStatistics statistic = new SummaryStatistics(activities.size())

        if (activities.size() > 0) {
            statistic.distance = activities.stats.totalDistanceMeters.sum()
            statistic.totalDurationSeconds = activities.stats.totalDurationSeconds.sum()
            statistic.sufferScore = activities.stats.sufferScore.sum()
            statistic.fromDate = activities[0].startDateTime
            statistic.displayText = displayText
            statistic.averageHR = activities.stats.averageHR.sum()
            statistic.averageSpeed = activities.stats.averageSpeed.sum()
        }
        statistic
    }


    List<Activity> recentActivities(UserProfile userProfile, int max = 5) {
        List<Activity> recentActivities = Activity.findAllByUserProfile(userProfile, [max: max, sort: "startDateTime", order: "desc"])
        recentActivities
    }

    List<Activity> allActivities(UserProfile userProfile, ActivityType activityType = null) {
        List<Activity> allActivities
        if (activityType) {
            allActivities = Activity.findAllByUserProfileAndActivityType(userProfile, activityType, [max: 5000, sort: "startDateTime", order: "asc"])
        } else {
            allActivities = Activity.findAllByUserProfile(userProfile, [max: 5000, sort: "startDateTime", order: "asc"])
        }
        allActivities
    }

    SummaryStatistics allTimeStatistics(UserProfile userProfile, ActivityType activityType = null) {
        List<Activity> allTimeActivities = allActivities(userProfile, activityType)

        def typeName = activityType ? activityType.name : ALL_ACTIVITIES_TEXT
        SummaryStatistics statistic = summaryStatsFromActivities(allTimeActivities, typeName)

        statistic
    }

    List<Activity> rollingYearActivities(UserProfile userProfile, ActivityType activityType = null) {
        Date yearAgo
        Date now = new Date()
        use(TimeCategory) {
            yearAgo = now - 1.year
        }
        def query = Activity.where {
            userProfile == userProfile
            if (activityType) {
                activityType == activityType
            }
            startDateTime >= yearAgo
            startDateTime <= now
        }
        List<Activity> yearActivities = query.list([max: 5000, sort: "startDateTime", order: "asc"])

        yearActivities
    }

    SummaryStatistics rollingYearStatistics(UserProfile userProfile, ActivityType activityType = null) {
        List<Activity> activities = rollingYearActivities(userProfile, activityType)
        def typeName = activityType ? activityType.name : ALL_ACTIVITIES_TEXT
        SummaryStatistics statistic = summaryStatsFromActivities(activities, typeName)
        statistic
    }

    List<Activity> rollingMonthActivities(UserProfile userProfile, ActivityType activityType = null) {
        Date monthAgo
        Date now = new Date()
        use(TimeCategory) {
            monthAgo = now - 1.month
        }
        def query = Activity.where {
            userProfile == userProfile
            if (activityType) {
                activityType == activityType
            }
            startDateTime >= monthAgo
            startDateTime <= now
        }
        List<Activity> activities = query.list([max: 5000, sort: "startDateTime", order: "asc"])

        activities
    }

    SummaryStatistics rollingMonthStatistics(UserProfile userProfile, ActivityType activityType = null) {
        List<Activity> activities = rollingMonthActivities(userProfile, activityType)
        def typeName = activityType ? activityType.name : ALL_ACTIVITIES_TEXT
        SummaryStatistics statistic = summaryStatsFromActivities(activities, typeName)
        statistic
    }

    List<Activity> rollingWeekActivities(UserProfile userProfile, ActivityType activityType = null) {
        Date weekAgo
        Date now = new Date()
        use(TimeCategory) {
            weekAgo = now - 1.week
        }
        def query = Activity.where {
            userProfile == userProfile
            if (activityType) {
                activityType == activityType
            }
            startDateTime >= weekAgo
            startDateTime <= now
        }
        List<Activity> activities = query.list([max: 5000, sort: "startDateTime", order: "asc"])

        activities
    }

    SummaryStatistics rollingWeekStatistics(UserProfile userProfile, ActivityType activityType = null) {
        List<Activity> activities = rollingWeekActivities(userProfile, activityType)
        def typeName = activityType ? activityType.name : ALL_ACTIVITIES_TEXT
        SummaryStatistics statistic = summaryStatsFromActivities(activities, typeName)
        statistic
    }

    /**
     * I DO NOT like this algorithm, its damn ugly and dangerous and not even right but its what I came up with to "zero-pad" out week grouped data if there is nothing there
     * @param userProfile
     * @return
     */
    def mainChartData(UserProfile userProfile) {
        Map groups = allActivities(userProfile).groupBy { it.startDateTime.format('ww-MM-yyyy') }  // sorted asc

        List<String> labels = []
        List<Integer> distances = []
        List<String> durations = []

        String lastKey
        groups.each { key, activities ->

            List thisDateSplit = key.split "-"
            int currentYear = thisDateSplit[2].toInteger()
            int currentWeek = thisDateSplit[0].toInteger()
            if (lastKey) {
                List lastDateSplit = lastKey.split "-"

                log.info "thisDateSplit = ${thisDateSplit}, lastDateSplit = ${lastDateSplit}"
                int priorYear = lastDateSplit[2].toInteger()
                int priorWeek = lastDateSplit[0].toInteger()

                while (currentYear >= priorYear) {
                    println "${currentYear} >= ${priorYear}"
                    if (currentYear > priorYear) {  // If need to fill in some zero weeks that cross the year boundary...
                        log.info "priorWeek: ${priorWeek}"
                        while (priorWeek++ < 52) {
                            labels += "'${FormatUtils.getDateFieldFromWeekOfYear(priorWeek, "MMM")} ${currentYear}'"
                            distances += 0
                            durations += 0
                            log.info "zero filling (yr rolling) wk:${priorWeek} ${FormatUtils.getDateFieldFromWeekOfYear(priorWeek, "MMM") }${priorYear}"
                        }
                        priorWeek = 0
                    }
                    while (++priorWeek < currentWeek) {  // Fill in for any on the same year
                        labels += "'${FormatUtils.getDateFieldFromWeekOfYear(priorWeek, "MMM")} ${currentYear}'"
                        distances += 0
                        durations += 0

                        /*zeroFilledGroups.put "${currentWeek}-00-${priorYear}", "0"*/
                        log.info "zero filling wk:${priorWeek} ${FormatUtils.getDateFieldFromWeekOfYear(priorWeek, "MMM") }${priorYear}"
                    }
                    priorYear++
                }
            }
            log.info "Adding wk:${currentWeek} ${FormatUtils.getDateFieldFromWeekOfYear(currentWeek, "MMM") }${currentYear}"
            labels += "'${FormatUtils.getDateFieldFromWeekOfYear(currentWeek, "MMM")} ${currentYear}'"
            distances += activities.stats.totalDistanceMeters.sum() / 1000
            // durations += "'${FormatUtils.secondsToHHMMSS(activities.stats.totalDurationSeconds.sum())}'"
            durations += activities.stats.totalDurationSeconds.sum()

            // zeroFilledGroups.put k, activities.name
            lastKey = key
            log.info "***** ${key}=${activities}"
        }

        [labels, distances, durations]
    }
}
