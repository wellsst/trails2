/**
 * Created with IntelliJ IDEA.
 * User: i079413
 * Date: 29/06/16
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */


(function () {

    angular
        .module('trails')
        .directive('overallStatsRow', function () {
            return {
                replace: 'true',
                //transclude: true,
                restrict: 'A', //E = element, A = attribute, C = class, M = comment
                scope: {
                    //@ reads the attribute value, = provides two-way binding, & works with functions
                    stats: '=',
                    activityType: '@'
                },
                //template: '<div>{{ myVal }}</div>',
                templateUrl: '/partials/overallStatsPartial'
                //controller: controllerFunction, //Embed a custom controller in the directive
                //link: function ($scope, element, attrs) { } //DOM manipulation
            }
        });
})();