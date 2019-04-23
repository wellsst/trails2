package trails

import trails.stats.BasicStats
import trails.stats.SpeedStatSet
import trails.stats.WeatherStat
import trails.user.UserProfile


class Activity {

    // String sourceFileName // to track if this has already been uploaded
    String name // eg running to somewhere
    String description
    Date startDateTime

    ActivityType activityType // eg Running, swimming

    UserProfile userProfile

    // Basic stats
    BasicStats stats
    WeatherStat weatherStats

    SpeedStatSet speedStatSet

    List segments
    List trainingZones
    static hasMany = [segments: Segment, trainingZones: ActivityTrainingZone] //trainingZones to store the overall data


    static constraints = {
        description nullable: true
        weatherStats nullable: true
        speedStatSet nullable: true
    }

    static mapping = {
        trainingZones sort: 'zoneIndex', order: 'asc'
    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", activityType=" + activityType +
                ", userProfile=" + userProfile +
                ", stats=" + stats +
                '}';
    }


}
