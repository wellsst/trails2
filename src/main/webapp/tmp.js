var app = angular.module('myApp', ['ngAnimate', 'ngSanitize', 'ui.bootstrap', 'ui.calendar']);
app.controller('myCtrl', function ($scope) {
    $scope.pb = {
        hours: 0,
        mins: 25,
        secs: 0
    };

    $scope.secsToHHMMSS = function (totalSecs, divisor = 0) {
        if (divisor > 0) {
            totalSecs = totalSecs / divisor;
        }
        var hours = parseInt(totalSecs / 3600) % 24;
        var minutes = parseInt(totalSecs / 60) % 60;
        var seconds = Math.round(totalSecs % 60);

        return padNumber(hours, 2) + ":" + padNumber(minutes, 2) + ":" + padNumber(seconds, 2);
    }

    $scope.calc = function () {
        // Put the whole thing into seconds
        var current = $scope.pb.hours * 3600 + $scope.pb.mins * 60 + $scope.pb.secs;
        // console.log(current);
        var improvers = [
            {pct: 0},
            {pct: 1},
            {pct: 1.5},
            {
                pct: 2
            },
            {
                pct: 2.5
            },
            {
                pct: 3
            }
        ]; // percentage to improve by
        $scope.aims = [];

        for (i in improvers) { // for each percent to improve ...
            //console.log(improvers[i]);
            var totalSecs = current * (100 - improvers[i].pct) / 100;
            //console.log("Total secs: " + totalSecs);
            var hours = parseInt(totalSecs / 3600) % 24;
            var minutes = parseInt(totalSecs / 60) % 60;
            var seconds = Math.round(totalSecs % 60);

            var speedWork1TotalSecs = totalSecs / 5.33
            $scope.aims.push({
                percentImprove: improvers[i].pct,
                goal: {
                    totalSecs: totalSecs,
                    hours: hours,
                    mins: minutes,
                    secs: seconds
                }/*,
                 speedWork1: {
                 hours: parseInt(speedWork1TotalSecs / 3600) % 24,
                 mins: parseInt(speedWork1TotalSecs / 60) % 60,
                 secs: Math.round(speedWork1TotalSecs % 60)
                 }*/
            })
        }

        $scope.predictors = [];

        var predictorDetails = [
            {label: "10km", factor: 2.078, cssClass: "progress-bar-success", width: 15, distanceKms: 10},
            {label: "Half-mara", factor: 4.64, cssClass: "progress-bar-warning", width: 30, distanceKms: 21.1},
            {label: "Marathon", factor: 9.75, cssClass: "progress-bar-danger", width: 55, distanceKms: 42.2},
        ];
        for (key in predictorDetails) {
            var predictorDetail = predictorDetails[key];
            var totalSecs = current * predictorDetail.factor; // predictors[i].value;
            //console.log("Total secs: " + totalSecs);
            var hours = parseInt(totalSecs / 3600) % 24;
            var minutes = parseInt(totalSecs / 60) % 60;
            var seconds = Math.round(totalSecs % 60);
            //console.log(key + " = " + predictorDetail + ", mins: " + minutes);

            var targetTotalSecs = totalSecs / predictorDetail.distanceKms;

            $scope.predictors.push({label: predictorDetail.label,
                cssClass: predictorDetail.cssClass,
                width: predictorDetail.width,
                goal: {
                    hours: hours,
                    mins: minutes,
                    secs: seconds
                },
                targetPace: {
                    hours: parseInt(targetTotalSecs / 3600) % 24,
                    mins: parseInt(targetTotalSecs / 60) % 60,
                    secs: Math.round(targetTotalSecs % 60)
                }
            })
        }
    };

    // Calendar stuff...
    /* config object */
    $scope.uiConfig = {
        calendar: {
            height: 450,
            editable: true,
            header: {
                left: 'month basicWeek basicDay agendaWeek agendaDay',
                center: 'title',
                right: 'today prev,next'
            },
            eventClick: $scope.alertEventOnClick,
            eventDrop: $scope.alertOnDrop,
            eventResize: $scope.alertOnResize
        }
    };

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    $scope.events = [
        {title: 'All Day Event', start: new Date(y, m, 1)},
        {title: 'Long Event', start: new Date(y, m, d - 5), end: new Date(y, m, d - 2)},
        {id: 999, title: 'Repeating Event', start: new Date(y, m, d - 3, 16, 0), allDay: false},
        {id: 999, title: 'Repeating Event', start: new Date(y, m, d + 4, 16, 0), allDay: false},
        {title: 'Birthday Party', start: new Date(y, m, d + 1, 19, 0), end: new Date(y, m, d + 1, 22, 30), allDay: false},

    ];

    /* alert on eventClick */
    $scope.alertOnEventClick = function (date, jsEvent, view) {
        $scope.alertMessage = (date.title + ' was clicked ');
    };
    /* alert on Drop */
    $scope.alertOnDrop = function (event, delta, revertFunc, jsEvent, ui, view) {
        $scope.alertMessage = ('Event Droped to make dayDelta ' + delta);
    };
    /* alert on Resize */
    $scope.alertOnResize = function (event, delta, revertFunc, jsEvent, ui, view) {
        $scope.alertMessage = ('Event Resized to make dayDelta ' + delta);
    };
    /* add and removes an event source of choice */
    $scope.addRemoveEventSource = function (sources, source) {
        var canAdd = 0;
        angular.forEach(sources, function (value, key) {
            if (sources[key] === source) {
                sources.splice(key, 1);
                canAdd = 1;
            }
        });
        if (canAdd === 0) {
            sources.push(source);
        }
    };
    /* add custom event*/
    $scope.addEvent = function () {
        $scope.events.push({
            title: 'Open Sesame',
            start: new Date(y, m, 28),
            end: new Date(y, m, 29),
            className: ['openSesame']
        });
    };
    /* remove event */
    $scope.remove = function (index) {
        $scope.events.splice(index, 1);
    };

    /* Render Tooltip */
    $scope.eventRender = function (event, element, view) {
        element.attr({'tooltip': event.title,
            'tooltip-append-to-body': true});
        $compile(element)($scope);
    };

    $scope.eventSources = [$scope.events];

    // ***** Do the calc from the start, also called on-change of values
    $scope.calc();
});

function padNumber(n, len) {
    var num = parseInt(n, 10);
    len = parseInt(len, 10);
    if (isNaN(num) || isNaN(len)) {
        return n;
    }
    num = '' + num;
    while (num.length < len) {
        num = '0' + num;
    }
    return num;
};

app.filter('numberFixedLen', function () {
    return function (n, len) {
        var num = parseInt(n, 10);
        len = parseInt(len, 10);
        if (isNaN(num) || isNaN(len)) {
            return n;
        }
        num = '' + num;
        while (num.length < len) {
            num = '0' + num;
        }
        return num;
    };
});