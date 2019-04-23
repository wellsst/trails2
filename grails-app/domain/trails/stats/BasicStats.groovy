package trails.stats

class BasicStats {

    BigDecimal totalDistanceMeters = 0
    BigDecimal totalElevationMeters = 0
    int totalDurationSeconds = 0
    int totalMovingTimeSeconds = 0    // todo: collect this stat in the ActivityService
    int sufferScore = 0

    int averageHR = 0
    BigDecimal averageSpeed = 0
    BigDecimal averagePace = 0

    //Activity activity

    /*static constraints = {
        activity nullable: true
    }*/

    @Override
    public String toString() {
        return "BasicStats{" +
                "totalDistanceMeters=" + totalDistanceMeters +
                ", totalElevationMeters=" + totalElevationMeters +
                ", totalDurationSeconds=" + totalDurationSeconds +
                ", averageHR=" + averageHR +
                ", averageSpeed=" + averageSpeed +
                ", averagePace=" + averagePace +
                ", sufferScore=" + sufferScore +
                '}';
    }
}
