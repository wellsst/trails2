package trails
import grails.transaction.Transactional
import groovy.json.JsonBuilder
import trails.service.ActivityService
import trails.service.StatisticsService
import trails.user.UserProfile
import utils.TrailsException

import javax.servlet.http.Part

import static org.springframework.http.HttpStatus.*

@Transactional(readOnly = true)
class ActivityController  {

    //static responseFormats = ['json', 'xml']

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    ActivityService activityService

    StatisticsService statisticsService

    /*ActivityController() {
        super(Activity)
    }*/

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        //log.debug Activity.get(2).speedStatSet.speedStats
        respond Activity.list(fetch:[speedStatSet:"join"]) //, model:[activityCount: Activity.count()]
    }

    def show(Activity activity) {
        respond activity
    }

    def create() {
        respond new Activity(params)
    }

    @Transactional
    def save(Activity activity) {
        if (activity == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (activity.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond activity.errors, view:'create'
            return
        }

        activity.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'activity.label', default: 'Activity'), activity.id])
                redirect activity
            }
            '*' { respond activity, [status: CREATED] }
        }
    }

    def edit(Activity activity) {
        respond activity
    }

    @Transactional
    def update(Activity activity) {
        if (activity == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (activity.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond activity.errors, view:'edit'
            return
        }

        activity.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'activity.label', default: 'Activity'), activity.id])
                redirect activity
            }
            '*'{ respond activity, [status: OK] }
        }
    }

    @Transactional
    def delete(Activity activity) {

        if (activity == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        activity.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'activity.label', default: 'Activity'), activity.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    def upload() {
        log.debug params
        List<Activity> activitiesUploaded = new ArrayList<>()
        String[] msgs = []
        if (params.gpxfile) {
            List<Part> fileParts = request.getParts()

            int filesProcessedCount = 0

            fileParts.each { filePart ->
                if (filePart.name == "gpxfile") {
                    log.debug "**** Importing filepart: ${filePart.contentType}, ${filePart.name} ${filePart.size}: ${filePart.inputStream.text.take(100)}"

                    // TODO update the get of userprofile once security is in place
                    try {
                        Activity activity = activityService.gpxImport(UserProfile.list()[0], filePart.inputStream.text)
                        activitiesUploaded.add activity
                        msgs += "Activity uploaded: ${activity.name}"
                    } catch (TrailsException te) {
                        msgs += te.msg
                    }

                    filesProcessedCount++
                }
            }
            // flash.message = "${filesProcessedCount} processed"
        }
        [messages: msgs, activitiesUploaded: activitiesUploaded]
    }

    def detailStats(Activity activity) {
        UserProfile userProfile = UserProfile.list()[0]

        /*def builder = new JsonBuilder()

        def root = builder.stats {
                    "activityTrackPoints" getTrackPointsAsPolyline(activity.segments)
                    //"activityStats" activity.stats
                    "name" activity.name
                    "typeName" activity.activityType.name
                    "startDateTime"activity.startDateTime
                    "speedStats" activity.speedStatSet.speedStats
        }
        //log.info builder.toPrettyString().encodeAsRaw()
        render builder.toPrettyString().encodeAsRaw()*/
        //json g.render(template:"activity", model:[activity:activity], contentType: "application/json")
        //json g.render(activity)
        respond activity
    }

    def personalBests() {
        UserProfile userProfile = UserProfile.list()[0]

        /*def c = Activity.createCriteria()
        def results = c.list {
            eq("userProfile",  userProfile)
            projections {
                groupProperty 'activityType'
            }
        }*/

        //log.debug "PB: ${results}"
        List<Activity> activities = Activity.findAllByUserProfile(userProfile)
        def pbList = activities.groupBy {it.activityType}

        pbList.each { activityType, activityList ->
            // println activityList.max {it.stats.totalDistanceMeters}

            println activityList.max {it.speedStatSet.speedStats.totalDistanceMeters}
        }

        respond activities
    }

    def overallStatsJSON(Integer max) {

        log.info "***** Grabbing overall stats..."

        params.max = Math.min(max ?: 10, 100)

        UserProfile userProfile = UserProfile.list()[0]

        //def (String timeData, String distanceData) = createTimeDistanceGraphData(userProfile)

        // def (labels, distances, durations) = statisticsService.mainChartData(userProfile)

        def builder = new JsonBuilder()

        // All of the "Other" stats
        List<ActivityType> otherActivityTypes = ActivityType.list() - userProfile.preferredActivityTypes

        def root = builder.stats {
            // Forever stats
            forever {
                // All activity types
                allTypes statisticsService.allTimeStatistics(userProfile)

                // All stats by activity
                userProfile.preferredActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.allTimeStatistics(userProfile, activityType)
                }

                otherActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.allTimeStatistics(userProfile, activityType)
                }

                /*rollingYearStatistics statisticsService.rollingYearStatistics(userProfile)
                rollingMonthStatistics statisticsService.rollingMonthStatistics(userProfile)
                rollingWeekStatistics statisticsService.rollingWeekStatistics(userProfile)*/
            }

            // Back a year from now...
            yearStats {
                // All activity types
                allTypes statisticsService.rollingYearStatistics(userProfile)

                // All stats by activity
                userProfile.preferredActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.rollingYearStatistics(userProfile, activityType)
                }

                otherActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.rollingYearStatistics(userProfile, activityType)
                }
            }
            // Back a month from now...
            monthStats {
                // All activity types
                allTypes statisticsService.rollingMonthStatistics(userProfile)

                // All stats by activity
                userProfile.preferredActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.rollingMonthStatistics(userProfile, activityType)
                }

                otherActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.rollingMonthStatistics(userProfile, activityType)
                }
            }
            // Back a week from now...
            weekStats {
                // All activity types
                allTypes statisticsService.rollingWeekStatistics(userProfile)

                // All stats by activity
                userProfile.preferredActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.rollingWeekStatistics(userProfile, activityType)
                }

                otherActivityTypes.each { activityType ->
                    "${activityType.name}" statisticsService.rollingWeekStatistics(userProfile, activityType)
                }
            }
        }

        // [allStats: builder.toPrettyString().encodeAsRaw()]
        render builder.toPrettyString().encodeAsRaw()
    }

    // Empty to get just the GSP to work for the angular include of this file,
    // otherwise we have a static html file that we need to restart grails each time to pickup changes
    def overallStats() {

    }
    def details() {

    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'activity.label', default: 'Activity'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
