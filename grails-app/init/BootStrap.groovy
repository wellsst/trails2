import trails.Activity
import trails.ActivityType
import trails.SearchWord
import trails.stats.BasicStats
import trails.stats.SpeedStat
import trails.stats.SpeedStatSet
import trails.user.TrainingZone
import trails.user.TrainingZoneSet
import trails.user.UserProfile

class BootStrap {

    def init = { servletContext ->
        /*def cloneForDomains = {
            def cloned = delegate.class.newInstance();
            cloned.properties = delegate.properties;
            return cloned;
        }
        org.grails.datastore.gorm.GormInstanceApi.clone = cloneForDomains;*/

        ActivityType runningType = new ActivityType(name: "Running")
        ActivityType cyclingType = new ActivityType(name: "Cycling")

        SpeedStatSet runningSpeedStatSet = new SpeedStatSet()
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "100m", distanceToCoverMeters: 100))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "500m", distanceToCoverMeters: 500))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "1km", distanceToCoverMeters: 1000))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "2km", distanceToCoverMeters: 2000))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "5km", distanceToCoverMeters: 5000))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "10km", distanceToCoverMeters: 10000))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "Half-marathon", distanceToCoverMeters: 21000))
        runningSpeedStatSet.addToSpeedStats(new SpeedStat(name: "Marathon", distanceToCoverMeters: 42000))

        // runningType.runningSpeedStatSet = runningSpeedStatSet

        TrainingZoneSet runningZoneSet = new TrainingZoneSet(name: "Running")
        runningZoneSet.addToTrainingZones(new TrainingZone(name: 'Warm up', zoneIndex: 1, lowerHeartRate: 0, upperHeartRate: 100, sufferK: 12))
        runningZoneSet.addToTrainingZones(new TrainingZone(name: 'Easy', zoneIndex: 2, lowerHeartRate: 101, upperHeartRate: 112, sufferK: 24))
        runningZoneSet.addToTrainingZones(new TrainingZone(name: 'Aerobic', zoneIndex: 3, lowerHeartRate: 113, upperHeartRate: 130, sufferK: 48))
        runningZoneSet.addToTrainingZones(new TrainingZone(name: 'Threshold', zoneIndex: 4, lowerHeartRate: 131, upperHeartRate: 175, sufferK: 96))
        runningZoneSet.addToTrainingZones(new TrainingZone(name: 'Maximum', zoneIndex: 5, lowerHeartRate: 176, upperHeartRate: 999, sufferK: 192))
        runningZoneSet.save()

        runningType.trainingZoneSet = runningZoneSet

        runningType.save()

        runningSpeedStatSet.activityType = runningType
        runningSpeedStatSet.save()

        // **** Make sure they are lowercase!!!!
        new SearchWord(keyword: "run", activityType: runningType).save()
        new SearchWord(keyword: "running", activityType: runningType).save()
        new SearchWord(keyword: "ran", activityType: runningType).save()

        // Just for now...

        SpeedStatSet cyclingSpeedStatSet = new SpeedStatSet()
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "100m", distanceToCoverMeters: 100))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "500m", distanceToCoverMeters: 500))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "1km", distanceToCoverMeters: 1000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "2km", distanceToCoverMeters: 2000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "5km", distanceToCoverMeters: 5000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "10km", distanceToCoverMeters: 10000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "20km", distanceToCoverMeters: 20000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "30km", distanceToCoverMeters: 30000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "50km", distanceToCoverMeters: 50000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "75km", distanceToCoverMeters: 75000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "100km", distanceToCoverMeters: 100000))
        cyclingSpeedStatSet.addToSpeedStats(new SpeedStat(name: "200km", distanceToCoverMeters: 200000))

        //cyclingType.runningSpeedStatSet = cyclingSpeedStatSet
        cyclingType.trainingZoneSet = runningZoneSet
        cyclingType.save()
        cyclingSpeedStatSet.activityType = cyclingType
        cyclingSpeedStatSet.save()

        println "*************************************"
        println cyclingType.errors

        new SearchWord(keyword: "ride", activityType: cyclingType).save()
        new SearchWord(keyword: "riding", activityType: cyclingType).save()
        new SearchWord(keyword: "rode", activityType: cyclingType).save()
        new SearchWord(keyword: "cycle", activityType: cyclingType).save()
        new SearchWord(keyword: "bike", activityType: cyclingType).save()

        UserProfile userProfile = new UserProfile()
        userProfile.addToTrainingZoneSets(runningZoneSet)

        userProfile.addToPreferredActivityTypes(runningType)
        userProfile.addToPreferredActivityTypes(cyclingType)

        SpeedStatSet userRunningSpeedStatSet = new SpeedStatSet(runningSpeedStatSet.properties)
        userRunningSpeedStatSet.userProfile = userProfile
        userRunningSpeedStatSet.activityType = runningSpeedStatSet.activityType

        runningSpeedStatSet.speedStats.each {
            userRunningSpeedStatSet.addToSpeedStats(new SpeedStat(it.properties))
        }
        userRunningSpeedStatSet.save()
        userProfile.addToSpeedStatSets(userRunningSpeedStatSet)

        SpeedStatSet userCyclingSpeedStatSet = new SpeedStatSet()
        userCyclingSpeedStatSet = new SpeedStatSet(cyclingSpeedStatSet.properties)
        userCyclingSpeedStatSet.userProfile = userProfile
        userCyclingSpeedStatSet.activityType = cyclingSpeedStatSet.activityType
        cyclingSpeedStatSet.speedStats.each {
            userCyclingSpeedStatSet.addToSpeedStats(new SpeedStat(it.properties))
        }
        userCyclingSpeedStatSet.save()
        userProfile.addToSpeedStatSets(userCyclingSpeedStatSet)

        if (!userProfile.save(flush: true)) {
            println "Could not save dummy data ${userProfile.errors}"
        }

        Activity activity = new Activity()
        activity.userProfile = userProfile
        activity.name = "test"
        activity.startDateTime = new Date()
        activity.activityType = runningType

        BasicStats stats = new BasicStats()
        stats.averageHR = 150
        stats.averagePace = 20
        stats.averageSpeed = 60
        stats.totalDistanceMeters = 1234
        stats.totalDurationSeconds = 60000
        stats.totalElevationMeters = 20
        stats.totalMovingTimeSeconds = 5000
        stats.sufferScore = 90

        /*Segment segment = new Segment()

        segment.stats = stats*/

        activity.stats = stats
        if (!activity.save(flush: true)) {
            println "Could not save dummy activity data ${activity.errors}"
        }

        println Activity.list()
    }
    def destroy = {
    }
}
