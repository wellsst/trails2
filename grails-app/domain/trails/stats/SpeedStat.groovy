package trails.stats

import trails.user.TrackPoint

/*
Like fastest 100m, 1000m of an activity
See http://royvanrijn.com/blog/2014/02/hacking-runkeeper-data/  for ideas
Could even keep track of every eg 100m segment for an activity?  too much?
 */
class SpeedStat extends BaseStat {
    BigDecimal distanceToCoverMeters
    int durationInSecs

    int secondsAfterStart = 0

    BigDecimal speedKmh = 0 // km/h
    BigDecimal pace = 0 // mins:secs/km

    List<TrackPoint> route
    // SortedSet route
    static hasMany = [route: TrackPoint]
    static fetchMode = [route: 'join']

    static constraints = {
        route nullable: true
    }

    BigDecimal distanceCoveredTrackerMeters = 0
    static transients = ['distanceCoveredTrackerMeters']


    @Override
    public String toString() {
        return "SpeedStat{" + name +
                "distanceToCoverMeters=" + distanceToCoverMeters +
                ", durationInSecs=" + durationInSecs +
                ", speedKmh=" + speedKmh +
                ", pace=" + pace +
                ", distanceCoveredTrackerMeters=" + distanceCoveredTrackerMeters +
                '}';
    }
}
