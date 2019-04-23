package trails.stats

abstract class BaseStat {
    String name
    String description

    Date recordedOn = new Date()

    static constraints = {
        description nullable: true
    }
}
