package trails

import trails.stats.BasicStats
import trails.user.TrackPoint

class Segment {

    int segmentIndex = 0

    BasicStats stats

    SortedSet trainingZones
    SortedSet trackPoints

    //trainingZones to store the per segment data
    static hasMany = [trainingZones: ActivityTrainingZone, trackPoints: TrackPoint]   // Can be pre-canned ones from the app or later user defined

    static belongsTo = [Activity]

    static constraints = {
    }

    static mapping = {
        trainingZones sort: 'zoneIndex', order: 'asc'
    }

    @Override
    public String toString() {
        "Segment ${segmentIndex}: ${stats}"
    }
}
