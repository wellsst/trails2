package trails.service

import grails.transaction.Transactional
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import trails.*
import trails.stats.BasicStats
import trails.stats.SpeedStat
import trails.stats.SpeedStatSet
import trails.user.TrackPoint
import trails.user.TrainingZoneSet
import trails.user.UserProfile
import utils.GeoCodeUtils
import utils.SummaryStatistics
import utils.TrailsException

import java.text.SimpleDateFormat

@Transactional
class ActivityService {

    Activity gpxImport(UserProfile userProfile, String gpxString) throws TrailsException {
        def gpxXml
        try {
            gpxXml = new XmlParser(false, false).parseText(gpxString)
        } catch (all) {
            throw new TrailsException(msg: "Invalid GPX file provided, ensure your GPX file is valid and your session has not timed out.")
        }

        List trackPointNodes = gpxXml.trk.trkseg

        // Keep track of the "last" zone info since we get the date/time diff from the next node to calc what the first one should be
        // Could interpolate the heart rates between the time samples but for simplicity lets just say that the first range "owns" all the ranges in between...close to accurate?  better than guess in-between values?

        Activity activity = new Activity(userProfile: userProfile)

        String activityName = gpxXml.trk.name.text()
        activity.name = activityName

        def activityDateString = gpxXml.metadata.time.text()
        Date activityStartDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(activityDateString) // 2015-11-14T23:15:21.000Z

        // Check by name and date we have not already uploaded
        if (Activity.countByStartDateTime(activityStartDate) > 0) {
            throw new TrailsException(msg: "Activity ${activityName} already exists based on datetime ${activityDateString}")
        }

        activity.startDateTime = activityStartDate

        activity.activityType = guessActivityType(activityName)
        TrainingZoneSet trainingZoneSet = activity.activityType.trainingZoneSet
        trainingZoneSet.trainingZones.each { trainingZone ->
            activity.addToTrainingZones(new ActivityTrainingZone(trainingZone: trainingZone, zoneIndex: trainingZone.zoneIndex))
        }

        // Copy the pre-canned speedKmh stats from the activity type, so we can fill in the numbers for the activity
        //activity.speedStatSet = SpeedStatSet.findByActivityType(activity.activityType)
        // Copy the pre-canned speedKmh stats from the activity type, so we can fill in the numbers for the activity
        // activity.speedStatSet = new SpeedStatSet()

        // todo: A little guard around this code needed
        activity.speedStatSet = new SpeedStatSet()

        activity.speedStatSet.userProfile = userProfile
        activity.speedStatSet.activityType = activity.activityType

        SpeedStatSet.findByUserProfileAndActivityType(userProfile, activity.activityType).speedStats.each { profileSpeedStat ->
            SpeedStat speedStat = new SpeedStat(profileSpeedStat.properties)
            activity.speedStatSet.addToSpeedStats(speedStat)
        }
        activity.speedStatSet.save()

        // If the user is importing from earlier dates then update their start date
        if (activity.startDateTime < userProfile.dateStarted) {
            userProfile.dateStarted = activity.startDateTime
        }

        log.info "****************** Importing ${gpxXml.trk.name.text()} on ${activityStartDate} ******************"
        int lastGoodHR = 0

        BasicStats activityStats = new BasicStats()
        BigDecimal totalDistanceMeters = 0
        BigDecimal totalElevationMeters = 0
        int averageHR = 0
        int totalTimeSeconds = 0

        TrackPoint lastTrackPoint

        // Init the segment for the activity
        int segmentCount = 1
        int segmentDurationSecs = 0
        int segmentDistanceMeters = 0
        Segment segment = initSegment(segmentCount, activity)

        HashMap<String, List<TrackPoint>> speedStatCache = new HashMap()

        int segmentTotalHR = 0 // used for average HR for the segment
        BigDecimal segmentTotalSpeed = 0 // used for average speed for the segment
        BigDecimal segmentTotalPace = 0 // used for average pace for the segment
        trackPointNodes[0].eachWithIndex { trackPointNode, idx ->

            // ******  1st processing step, gather basic data for the trackpoint
            TrackPoint trackPoint = new TrackPoint()

            Date trkptDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(trackPointNode.time.text())
            trackPoint.dateTime = trkptDate

            def hrNode = trackPointNode.depthFirst().find {
                it.name() == "gpxtpx:hr"
            }?.value() // the nodes in a Garmin gpx for heartrates, use the last known HR for times when it skips
            int hr = 0
            if (hrNode) {
                hr = hrNode[0]?.toInteger() ?: lastGoodHR
            }
            trackPoint.heartRate = hr
            segmentTotalHR += hr

            BigDecimal lon = trackPointNode.@lon.toBigDecimal()
            trackPoint.lon = lon
            BigDecimal lat = trackPointNode.@lat.toBigDecimal()
            trackPoint.lat = lat

            // calculate the distance between 2 points
            BigDecimal distanceTravelled = 0
            int durationInSecs = 1
            if (lastTrackPoint) {
                BigDecimal ele = new BigDecimal(trackPointNode.ele[0].text())
                trackPoint.elevation = ele
                if (trackPoint.elevation && lastTrackPoint.elevation && trackPoint?.elevation > lastTrackPoint?.elevation) {
                    activityStats.totalElevationMeters += trackPoint.elevation - lastTrackPoint.elevation
                    segment.stats.totalElevationMeters += trackPoint.elevation - lastTrackPoint.elevation
                }

                // Calc the distance traveled since the last point
                // todo: recode  distanceBetween for BigDecimal
                distanceTravelled = GeoCodeUtils.distanceBetween(lastTrackPoint.lat.toDouble(), trackPoint.lat.toDouble(),
                        lastTrackPoint.lon.toDouble(), trackPoint.lon.toDouble(),
                        lastTrackPoint.elevation?.toDouble() ?: 0.0d, trackPoint.elevation?.toDouble() ?: 0.0d)

                // Calc the total time for this zone
                TimeDuration duration = TimeCategory.minus(trackPoint.dateTime, lastTrackPoint.dateTime)
                durationInSecs = duration.toMilliseconds() / 1000
                activityStats.totalDurationSeconds += durationInSecs
                segment.stats.totalDurationSeconds += durationInSecs

                /*BigDecimal pace = 1000 / distanceTravelled * durationInSecs
                // ${new TimeDuration(0, pace/60, pace%60, 00)}
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);
                df.setMinimumFractionDigits(0);
                df.setGroupingUsed(false);
                log.info "Duration ${durationInSecs}, Distance in m: ${df.format(distanceTravelled)} pace raw: ${df.format(pace)}, ${(pace/60) as int}:${(pace as int) % 60}"*/
            }
            trackPoint.distance = distanceTravelled
            activityStats.totalDistanceMeters += distanceTravelled
            segment.stats.totalDistanceMeters += distanceTravelled

            // TODO: Could have been the last trackpoint in this HR zone, or we could do some averaging?  Close enough?
            trackPoint.durationInSecs = durationInSecs
            totalTimeSeconds += durationInSecs

            if (distanceTravelled > 0 && durationInSecs > 0) {
                trackPoint.speedKmh = (distanceTravelled / durationInSecs) * 3.6
                trackPoint.pace = 60 / trackPoint.speedKmh

                segmentTotalSpeed += trackPoint.speedKmh
                segmentTotalPace += trackPoint.pace

                log.debug "Average (${idx}): ${segment.stats}"
            }

            // Segment stats gathering phase...
            //
            // ****** Gather stats for speedKmh over certain distances
            activity.speedStatSet.speedStats.each { speedStat -> // for each activity
                List<TrackPoint> trackPointsCache = speedStatCache.get(speedStat.name)
                speedStat.distanceCoveredTrackerMeters += distanceTravelled

                log.debug " + Speed stat ${speedStat.name}, accum: ${speedStat.distanceCoveredTrackerMeters}, last dist ${trackPoint.distance}"

                if (!trackPointsCache) {
                    trackPointsCache = []
                }
                while (speedStat.distanceCoveredTrackerMeters - trackPoint.distance > speedStat.distanceToCoverMeters && trackPointsCache.size() > 0) {
                    def cachedDistance = trackPointsCache.remove(0).distance
                    speedStat.distanceCoveredTrackerMeters -= cachedDistance
                    log.debug "   -- Speed stat ${speedStat.name}, DE-accum: ${speedStat.distanceCoveredTrackerMeters} - ${cachedDistance}"
                }
                trackPointsCache.add(trackPoint)

                if (speedStat.distanceCoveredTrackerMeters >= speedStat.distanceToCoverMeters) {
                    log.debug "  *** New speedKmh stat ${speedStat.name}, ${speedStat.distanceCoveredTrackerMeters} > ${speedStat.distanceToCoverMeters}"
                    speedStat.route = trackPointsCache // todo:  will this work or we need a copy???

                    // Calculate the duration from the trackpoints in the route
                    BigDecimal speedStatDuration = 0
                    BigDecimal speedStatDistance = 0
                    trackPointsCache.each { trackPointInRoute ->
                        speedStatDuration += trackPointInRoute.durationInSecs
                        speedStatDistance += trackPointInRoute.distance
                    }
                    speedStat.durationInSecs = speedStatDuration
                    speedStat.speedKmh = (speedStatDistance / speedStatDuration) * 3.6
                    if (speedStat.speedKmh > 0) {
                        speedStat.pace = 60 / speedStat.speedKmh
                    }
                    // todo:  fix this, its
                    // speedStat.secondsAfterStart = (trackPointsCache[0].dateTime.time - activity.segments[0].trackPoints[0].dateTime.time) / 1000

                    // There may not yet be any trackpoints yet but we need to set the seconds after start to essentially 0
                    long startTime = activity.segments[0].trackPoints ? activity.segments[0].trackPoints[0].dateTime.time : trackPoint.dateTime.time
                    speedStat.secondsAfterStart = (trackPoint.dateTime.time - startTime) / 1000
                    speedStat.save()
                }
                speedStatCache.put(speedStat.name, trackPointsCache)
            }

            // ****** LAST Segment processing step:  Update the segment and reset for a new one
            if (segment.stats.totalDistanceMeters > userProfile.segmentLengthMeters) {
                updateSegmentStats(segment, segmentTotalHR, segmentTotalSpeed, segmentTotalPace)
                log.info "Segment stats: ${segment.stats}"

                segment.save()

                //Re-init the segment
                segment = initSegment(++segmentCount, activity)
                segmentTotalHR = 0
                segmentTotalSpeed = 0
                segmentTotalSpeed = 0
                //segmentDistanceMeters = 0
            }

            segment.addToTrackPoints(trackPoint)

            // Find the zone for this heartrate and add up the total duration
            //TrainingZone trainingZone = TrainingZone.findByNameAndLowerHeartRateGreaterThanAndUpperHeartRateLessThanEquals(keywordForTrainingZone, hr, hr)
            ActivityTrainingZone segmentActivityTrainingZone = segment.trainingZones.find { tz ->
                hr >= tz.trainingZone.lowerHeartRate && hr <= tz.trainingZone.upperHeartRate
            }
            if (segmentActivityTrainingZone) {
                segmentActivityTrainingZone.secondsInZone += durationInSecs

            } else {
                log.warn "No segment training zone found for heartrate: ${hr}"
            }

            ActivityTrainingZone activityTrainingZone = activity.trainingZones.find { tz ->
                hr >= tz.trainingZone.lowerHeartRate && hr <= tz.trainingZone.upperHeartRate
            }
            if (activityTrainingZone) {
                activityTrainingZone.secondsInZone += durationInSecs

            } else {
                log.warn "No activity training zone found for heartrate: ${hr}"
            }
            lastTrackPoint = trackPoint
        }
        log.info "Speed stats: ${activity.speedStatSet.speedStats}"
        updateSegmentStats(segment, segmentTotalHR, segmentTotalSpeed, segmentTotalPace)

        // Update the overall stats
        activity.speedStatSet.speedStats.each { activitySpeedStat ->
            // Find a matching distance
            SpeedStat overallSpeedStat = userProfile.personalBests.speedStats.flatten().find { userSpeedStat ->
                userSpeedStat.distanceToCoverMeters.equals(activitySpeedStat.distanceToCoverMeters)
            }

            if (overallSpeedStat) {
                // Is this one quicker?
                if (activitySpeedStat.durationInSecs < overallSpeedStat.durationInSecs) {
                    overallSpeedStat = activitySpeedStat
                    overallSpeedStat.save()
                }
            } else { // Nothing there then create it
                SpeedStatSet personalBest = new SpeedStatSet()
                personalBest.activityType = activity.speedStatSet.activityType
                SpeedStat speedStat = new SpeedStat(activitySpeedStat.properties)
                personalBest.addToSpeedStats(speedStat)
                userProfile.personalBests.add(personalBest)
                userProfile.save()
            }
        }

        // Calc the overall suffer score
        activity.trainingZones.each { trainingZone ->
            activityStats.sufferScore += calcSuffer(trainingZone)
        }

        activityStats.averagePace = activity.segments.stats.averagePace.sum() / activity.segments.size()
        activityStats.averageSpeed = activity.segments.stats.averageSpeed.sum() / activity.segments.size()
        activityStats.averageHR = activity.segments.stats.averageHR.sum() / activity.segments.size()

        activity.stats = activityStats

        if (!activity.save(flush: true)) {
            log.error "Could not save activity ${activity.errors}"
        }

        activity
    }

    private void updateSegmentStats(Segment segment, int segmentTotalHR, BigDecimal segmentTotalSpeed, BigDecimal segmentTotalPace) {
//segment.stats.totalDistanceMeters = segmentDistanceMeters
        /*BigDecimal segmentKmh = (segment.stats.totalDistanceMeters / segment.stats.totalDurationSeconds) * 3.6

        BigDecimal segmentPace = 60 / segmentKmh
        // ${new TimeDuration(0, pace/60, pace%60, 00)}
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        log.info "Segment Duration ${segment.stats.totalDurationSeconds}, Distance in m: ${df.format(segment.stats.totalDurationSeconds)} pace raw: ${df.format(segmentPace)}, speed: ${segmentKmh}"
*/
        // Calculate the averages...the totals are accumulated as we go
        def nrTrackpoints = segment.trackPoints ? segment.trackPoints.size() : 1
        segment.stats.averageHR = segmentTotalHR / nrTrackpoints
        segment.stats.averageSpeed = segmentTotalSpeed / nrTrackpoints
        segment.stats.averagePace = segmentTotalPace / nrTrackpoints

        /*activityStats.averageSpeed = (activityStats.averageSpeed + segment.stats.averageSpeed) / idx + 1
        activityStats.averagePace = (activityStats.averagePace + segment.stats.averagePace) / idx + 1
        activityStats.averageHR = (activityStats.averageHR + segment.stats.averageHR) / idx + 1*/

        segment.trainingZones.each { trainingZone ->
            segment.stats.sufferScore += calcSuffer(trainingZone)
        }
    }

    /**
     * If we are breaking any records or not
     * @param activityType
     */
    def calculateTotalSummaryStats(Activity activity) {
        SummaryStatistics stats = new SummaryStatistics()

        /*List<Activity> allActivities = Activity.findAllByActivityType(activityType)

        double[] averagePaces
        allActivities.segments.each { segment ->
            segment.stats
        }
        stats.*/
    }

    private int calcSuffer(ActivityTrainingZone trainingZone) {
        trainingZone.trainingZone.sufferK * (trainingZone.secondsInZone / (60 * 60))  // Cacl based on hours in zone on the coefficient K (not secs)
    }

    private Segment initSegment(int segmentCount, Activity activity) {
        Segment segment = new Segment(segmentIndex: segmentCount)
        segment.stats = new BasicStats()
        activity.addToSegments(segment)

        // Setup the training zones, from the activity type we can get the TrainingZoneSet to use as a base
        // these copied zones can then be used to accumulate values for each zone for the activity
        TrainingZoneSet trainingZoneSet = activity.activityType.trainingZoneSet
        trainingZoneSet.trainingZones.each { trainingZone ->
            segment.addToTrainingZones(new ActivityTrainingZone(trainingZone: trainingZone, zoneIndex: trainingZone.zoneIndex))
        }
        segment
    }

    ActivityType guessActivityType(String keywords) {
        List<String> keywordList = keywords.split(/(,)|(;)|(\s)/).toList()

        // Remove rubbish words
        List<String> removeWordList =
            ['a', 'about', 'all', 'and', 'at', 'be', 'by', 'can', 'do', 'down', 'for', 'he', 'i', 'in', 'is', 'me', 'of', 'on', 'or', 'she', 'that',
                    'the', 'this', 'to', 'too', 'up', 'use', 'was', 'will', 'with', 'you', 'you', 'your']

        keywordList = keywordList*.toLowerCase()
        keywordList.removeAll { keyword ->
            removeWordList.contains(keyword)
        }

        List<SearchWord> keywordsFound = SearchWord.findAllByKeywordInList keywordList

        log.info "keywordsFound: ${keywordsFound}"

        List<ActivityType> possibleActivityTypes = keywordsFound*.activityType //.unique()

        /* List<ActivityType> possibleActivityTypes = ActivityType.findAllByInList(keywordList)*/
        log.info "Guess activity type from words: ${keywordList}, found ${possibleActivityTypes}"

        if (!possibleActivityTypes) {
            // TODO: Could add a new domain to hold keywords to search by
            // possibleActivityTypes = ActivityType.findAllByKeywordsInList(keywordList)

            // Just grab the default
            return ActivityType.list()[0]
        }
        possibleActivityTypes[0]
    }
}
