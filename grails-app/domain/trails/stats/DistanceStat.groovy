package trails.stats

import trails.user.TrackPoint

/**
 * eg Longest ever run @ 18km
 */
class DistanceStat extends BaseStat {

    TrackPoint start
    TrackPoint finish

    static constraints = {
    }
}
