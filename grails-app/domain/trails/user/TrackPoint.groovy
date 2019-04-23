package trails.user

import trails.Segment

class TrackPoint implements Comparable {

    int heartRate
    Date dateTime

    BigDecimal lat
    BigDecimal lon

    BigDecimal elevation // Elevation

    BigDecimal distance // from prior trackpoint

    int durationInSecs = 0  // Since a device recorded track point may last for arbitrary nr seconds we'll record this to know how long the HR was in this zone

    BigDecimal speedKmh = 0
    BigDecimal pace = 0   // mm:ss

    Segment segment
    static belongsTo = [Segment]

    static constraints = {
        heartRate nullable: true
        lat nullable: true, precision: 20, scale: 16
        lon nullable: true, precision: 20, scale: 16
        elevation nullable: true, precision: 20, scale: 16
        distance nullable: true, scale: 8
    }

    String asLatLonPolyString() {
        "{lat:${lat},lng:${lon}}"
    }

    String asLatLonMapString() {
        "[${lat},${lon}]"
    }

    List<BigDecimal> asLatLon() {
        [lat, lon]
    }


    @Override
    int compareTo(Object o) {
        this.dateTime <=> o.dateTime
    }

}
