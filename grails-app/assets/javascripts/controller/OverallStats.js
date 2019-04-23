/**
 * Created with IntelliJ IDEA.
 * User: i079413
 * Date: 6/06/16
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
(function () {

    angular
        .module('trails')
        .controller('OverallStatsController', [
            '$mdSidenav', '$mdBottomSheet', '$timeout', '$log', '$http',
            OverallStatsController
        ]);

    /**
     * Main Controller for the Angular Material Starter App
     * @param $scope
     * @param $mdSidenav
     * @param avatarsService
     * @constructor
     */
    function OverallStatsController($mdSidenav, $mdBottomSheet, $timeout, $log, $http) {
        var self = this;

        $log.debug("OverallStatsController loading...");

        self.row = [];
        //self.allStats = allStats;

        $http.get('/activity/overallStatsJSON', {
                params: { max: "100" }
            })
            .success(function (data) {
                self.allStats = data;
            })
            .error(function (data, status) {
                console.error('https error', status, data);
            })
            .finally(function () {
            });

    }

})();

