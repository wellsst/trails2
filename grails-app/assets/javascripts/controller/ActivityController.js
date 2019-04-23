/**
 * Created with IntelliJ IDEA.
 * User:
 * Date: 6/06/16
 * Time: 4:39 PM
 */
(function(){

    angular
        .module('trails')
        .controller('ActivityController', [
            '$mdSidenav', '$mdBottomSheet', '$timeout', '$log', '$http', '$location',
            ActivityController
        ]);

    /**
     * Main Controller for the Angular Material Starter App
     * @param $scope
     * @param $mdSidenav
     * @param avatarsService
     * @constructor
     */
    function ActivityController( $mdSidenav, $mdBottomSheet, $timeout, $log, $http, $location) {
        var self = this;

        $log.debug("ActivityController loading...");

        self.selected     = null;

        //self.allStats = allStats;

        $http.get('/activity/index.json', {
            params: { max: "1000" }
        })
            .success(function (data) {
                self.recentActivities = data;

                var speedStats = alasql('SEARCH /+speedStats/ FROM ?', [data]);
                var pbList = alasql('select min(durationInSecs) FROM ?', [speedStats]);

                var res = alasql('SELECT name FROM ? GROUP BY activityType->id',[data]);

                console.log(res); // [{"a":1,"b":40},{"a":2,"b":20}]

            })
            .error(function (data, status) {
                console.error('https error', status, data);
            })
            .finally(function () {
            });

       /* $http.get('/activity/personalBests.json', {
            params: { max: "1000" }
        })
            .success(function (data) {
                self.overallStats = data;

            })
            .error(function (data, status) {
                console.error('https error', status, data);
            })
            .finally(function () {
            });*/


        function log() {
            $log.debug("Log at: " + new Date());
        }


        self.selectActivity = selectActivity;
        function selectActivity(activity) {
            $log.debug("Activity details: " + activity.id);
            $location.path("/activity/show/"+activity.id);
        }

        // *********************************
        // Internal methods
        // *********************************



        /**
         * Hide or Show the 'left' sideNav area
         */
        function toggleUsersList() {
            $mdSidenav('left').toggle();
        }

        /**
         * Select the current avatars
         * @param menuId
         */
        function selectUser ( user ) {
            self.selected = angular.isNumber(user) ? $scope.users[user] : user;
        }

        /**
         * Show the Contact view in the bottom sheet
         */
        function makeContact(selectedUser) {

            $mdBottomSheet.show({
                controllerAs  : "vm",
                templateUrl   : './src/users/view/contactSheet.html',
                controller    : [ '$mdBottomSheet', ContactSheetController],
                parent        : angular.element(document.getElementById('content'))
            }).then(function(clickedItem) {
                    $log.debug( clickedItem.name + ' clicked!');
                });

            /**
             * User ContactSheet controller
             */
            function ContactSheetController( $mdBottomSheet ) {
                this.user = selectedUser;
                this.items = [
                    { name: 'Phone'       , icon: 'phone'       , icon_url: 'assets/svg/phone.svg'},
                    { name: 'Twitter'     , icon: 'twitter'     , icon_url: 'assets/svg/twitter.svg'},
                    { name: 'Google+'     , icon: 'google_plus' , icon_url: 'assets/svg/google_plus.svg'},
                    { name: 'Hangout'     , icon: 'hangouts'    , icon_url: 'assets/svg/hangouts.svg'}
                ];
                this.contactUser = function(action) {
                    // The actually contact process has not been implemented...
                    // so just hide the bottomSheet

                    $mdBottomSheet.hide(action);
                };
            }
        }

    }

})();

