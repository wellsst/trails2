/**
 * Created with IntelliJ IDEA.
 * User: i079413
 * Date: 6/06/16
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */


'use strict';

var app = angular
    .module('trails', ['ngMaterial', 'md.data.table', 'ngRoute', 'ngMap','highcharts-ng'])
    .config(function($mdThemingProvider, $mdIconProvider){
        $mdThemingProvider.theme('default')
            .primaryPalette('lime')
            .accentPalette('deep-orange');
    });

app.config(function ($routeProvider, $locationProvider) {

    $routeProvider
        .when('/activity/index', {
            templateUrl: '/activity/overallStats',
            controller: 'OverallStatsController',
            controllerAs: 'overallStats'
        })
        .when('/', {
            templateUrl: '/activity/overallStats',
            controller: 'OverallStatsController',
            controllerAs: 'overallStats'
        })

        .when('/activity/show/:id', {
            templateUrl: '/activity/details',
            controller: 'ActivityDetailsController',
            controllerAs: 'actDetails'
        })
        .otherwise({redirectTo: '/asd'});


    // Enable 'HTML5 History API' mode for URLs.
    // Note this requires URL Rewriting on the server-side. Leave this
    // out to just use hash URLs `/#/athletes/1/edit`
    //$locationProvider.html5Mode(true);

});

app.run([
    '$rootScope',
    function($rootScope) {
        // see what's going on when the route tries to change
        $rootScope.$on('$locationChangeStart', function(event, newUrl, oldUrl) {
            // both newUrl and oldUrl are strings
            console.log('Starting to leave %s to go to %s', oldUrl, newUrl);
        });
    }
]);
app.run([
    '$rootScope',
    function($rootScope) {
        // see what's going on when the route tries to change
        $rootScope.$on('$locationChangeSuccess', function(event, newUrl, oldUrl) {
            // both newUrl and oldUrl are strings
            console.log('$locationChangeSuccess %s to go to %s', oldUrl, newUrl);
        });
    }
]);
app.run([
    '$rootScope',
    function($rootScope) {
        // see what's going on when the route tries to change
        $rootScope.$on('$locationChangeError', function(event, newUrl, oldUrl, rejection) {
            // both newUrl and oldUrl are strings
            console.log('Error %s to go to %s: %s', oldUrl, newUrl, rejection);
        });
    }
]);