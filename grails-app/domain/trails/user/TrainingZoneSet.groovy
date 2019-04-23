package trails.user

class TrainingZoneSet {

    String name
    String description

    static hasMany = [trainingZones: TrainingZone]

    static constraints = {
        description nullable: true
    }

    static mapping = {
        trainingZones sort: 'zoneIndex', order: 'asc'
    }
}
