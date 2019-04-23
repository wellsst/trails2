package trails.user

import trails.Activity
import trails.ActivityType
import trails.stats.SpeedStatSet

class UserProfile {

    Date dateStarted = new Date()

    int segmentLengthMeters = 1000

    // lactate threshold heartrate could be used to calc some TRIMP scores and HRR - heart rate reserve, see: http://www.datacranker.com/heart-rate-zones/
    // The user needs to work this out for themselves based on some self applied tests at above website
    int lthr = 180

    // preferredActivityTypes is a subset of the built in types, sed mostly for display the most interesting types for a user,
    // ActivityTypes not in this list will be summarized in the "Other" listings
    // Precanned set of speed stats for the user which they should be able to edit
    static hasMany = [activities: Activity, trainingZoneSets: TrainingZoneSet, preferredActivityTypes: ActivityType, personalBests: SpeedStatSet, speedStatSets: SpeedStatSet]

    static constraints = {
        personalBests nullable: true
    }
}
