package trails.stats

class WeatherStat extends BaseStat {

    WindDirection windDirection
    float temperature
    float humidity
    String cloudConditions

    static constraints = {
        humidity nullable: true
        cloudConditions nullable: true
    }
}
