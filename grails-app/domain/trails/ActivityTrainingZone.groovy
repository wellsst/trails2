package trails

import trails.user.TrainingZone

class ActivityTrainingZone implements Comparable {

    int secondsInZone = 0

    int zoneIndex // for ordering, copied from the training zone when this is created

    TrainingZone trainingZone

    //Activity activity

    Segment segment
    Activity activity
    static belongsTo = [Segment, Activity]

    static constraints = {
        segment nullable: true
        activity nullable: true
    }

    static mapping = {
        sort zoneIndex: "asc"
    }


    @Override
    public String toString() {
        return "ActivityTrainingZone{" +
                "secondsInZone=" + secondsInZone +
                ", trainingZone=" + trainingZone +
                '}';
    }

    @Override
    int compareTo(Object o) {
        this.zoneIndex <=> o.zoneIndex
    }
}
