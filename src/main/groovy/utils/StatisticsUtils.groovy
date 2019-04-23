package utils

/**
 * Created by Ariba
 * User: i079413
 * Date: 15/04/2016
 * Time: 9:19 AM
 */
class StatisticsUtils {

    double approxRollingAverage (double avg, double new_sample) {

        avg -= avg / N;
        avg += new_sample / N;

        return avg;
    }
}
