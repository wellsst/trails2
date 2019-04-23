package trails

import trails.user.TrainingZoneSet

class ActivityType {

    String name
    // String keywords    // TODO: Could add a new domain to hold keywords to search by
    //List<SearchWord> keywords

    TrainingZoneSet trainingZoneSet // The associatied zones setup for this activity type
    //SpeedStatSet speedStatSet // Speed stat setup for this activity type

    //List<BasicStats> stats = new ArrayList() // Forever type stats
    int allTimesPerformed = 0  // Good also for tracking the averages

    //SpeedStat allSpeedStats = new SpeedStat()

    // These need to be a list of stats since we keep a stat
    /*BasicStats yearStats // Keep track of this year
    int yearTimesPerformed = 0*/
    //SpeedStat yearSpeedStats = new SpeedStat()

    /*BasicStats monthStats // Keep track of month ie last 4 weeks
    int monthTimesPerformed = 0*/
   // SpeedStat monthSpeedStats = new SpeedStat()

    /*BasicStats weekStats // Rolling week
    int weekTimesPerformed = 0*/
    //SpeedStat weekSpeedStats = new SpeedStat()

    // Roll any prior stats into these collections
   /* List<BasicStats> priorYears
    List<BasicStats> priorMonths
    List<BasicStats> priorWeeks*/

    //static hasMany = [activities: Activity]

    // static belongsTo = [Activity]

    static constraints = {
        //keywords nullable: true
        /*allStats nullable: true
        yearStats nullable: true
        monthStats nullable: true
        weekStats nullable: true*/
    }

    @Override
    public String toString() {
        name
    }
}
