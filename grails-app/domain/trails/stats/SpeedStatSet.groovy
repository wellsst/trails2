package trails.stats

import trails.ActivityType
import trails.user.UserProfile

class SpeedStatSet {

    ActivityType activityType

    UserProfile userProfile

    List<SpeedStat> speedStats

    static hasMany = [speedStats: SpeedStat]

    static constraints = {
        userProfile nullable: true
    }


    @Override
    public java.lang.String toString() {
        return "SpeedStatSet{" +
                "activityType=" + activityType +
                ", userProfile=" + userProfile +
                ", speedStats=" + speedStats +
                '}';
    }
}
