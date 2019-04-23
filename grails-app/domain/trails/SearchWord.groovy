package trails

/**
 * Created by Ariba
 * User: i079413
 * Date: 13/06/16
 * Time: 11:09 AM
 */
class SearchWord {
    String keyword

    ActivityType activityType

    // static belongsTo = [activityType: ActivityType]
    /*static mapping = {
        activityType column: '`activityType`'
    }*/

    /*static constraints = {
        activityType nullable: true
    }*/


    @Override
    public java.lang.String toString() {
        return "SearchWord{" +
                "keyword='" + keyword + '\'' +
                ", activityType=" + activityType +
                '}';
    }
}
