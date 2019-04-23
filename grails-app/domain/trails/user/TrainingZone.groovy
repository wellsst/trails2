package trails.user

class TrainingZone {

    String name

    int zoneIndex // for ordering

    String description
    int lowerHeartRate
    int upperHeartRate

    int sufferK // the suffer score coeffiecient like used in Strava and disected here: http://djconnel.blogspot.com.au/2011/08/strava-suffer-score-decoded.html

    TrainingZoneSet trainingZoneSet
    static belongsTo = [trainingZoneSet: TrainingZoneSet]

    static constraints = {
        description nullable: true
    }

    static mapping = {
        sort zoneIndex: "asc"
    }

    @Override
    public String toString() {
        return "TrainingZone{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lowerHeartRate=" + lowerHeartRate +
                ", upperHeartRate=" + upperHeartRate +
                '}';
    }
}
