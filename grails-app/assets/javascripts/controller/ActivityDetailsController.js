/**
 * Created with IntelliJ IDEA.
 * User:
 * Date: 6/06/16
 * Time: 4:39 PM
 */
(function () {

    angular
        .module('trails')
        .controller('ActivityDetailsController', [
            '$mdSidenav', '$mdBottomSheet', '$timeout', '$log', '$http', '$location', '$routeParams', 'NgMap',
            ActivityDetailsController
        ]);

   /* angular
        .module('trails')
        .controller('MyController', function (NgMap) {
            NgMap.getMap().then(function (map) {
                console.log(map.getCenter());
                console.log('markers', map.markers);
                console.log('shapes', map.shapes);
            });
        });*/

    /**
     * Main Controller for the Angular Material Starter App
     * @param self
     * @param $mdSidenav
     * @param avatarsService
     * @constructor
     */
    function ActivityDetailsController($mdSidenav, $mdBottomSheet, $timeout, $log, $http, $location, $routeParams, NgMap) {
        var self = this;

        $log.debug("ActivityDetailsController loading: " + $routeParams.id);

        self.selected = null;
        self.googleMapsUrl = "https://maps.googleapis.com/maps/api/js?key=";

        self.map = {
            center: [25, -70],
            options: function () {
                return {
                    zoom: 5,
                    streetViewControl: false,
                    scrollwheel: false
                }
            }
        };

        /*$http.get('/activity/show/' + $routeParams.id, {
            params: { max: "1000" }
        })
            .success(function (data) {
                self.activity = data;
            })
            .error(function (data, status) {
                console.error('https error', status, data);
            })
            .finally(function () {
            });*/

        self.hrList = [];
        self.elevationList = [];
        self.speedList = [];
        self.distanceList = [];

        /*self.data = {
            labels: self.distanceList,
            datasets: [
                {
                    label: 'HR',
                    fillColor: 'rgba(220,220,220,0.2)',
                    strokeColor: 'rgba(220,220,220,1)',
                    pointColor: 'rgba(220,220,220,1)',
                    pointStrokeColor: '#fff',
                    pointHighlightFill: '#fff',
                    pointHighlightStroke: 'rgba(220,220,220,1)',
                    spanGaps: true,
                    data: self.hrList
                },
                {
                    label: 'Speed',
                    fillColor: 'rgba(151,187,205,0.2)',
                    strokeColor: 'rgba(151,187,205,1)',
                    pointColor: 'rgba(151,187,205,1)',
                    pointStrokeColor: '#fff',
                    pointHighlightFill: '#fff',
                    pointHighlightStroke: 'rgba(151,187,205,1)',
                    spanGaps: true,
                    data: self.speedList
                },
                {
                    label: 'Elevation',
                    fillColor: 'rgba(100,230,120,0.3)',
                    strokeColor: 'rgba(151,187,205,1)',
                    pointColor: 'rgba(151,187,205,1)',
                    pointStrokeColor: '#fff',
                    pointHighlightFill: '#fff',
                    pointHighlightStroke: 'rgba(151,187,205,1)',
                    spanGaps: true,
                    data: self.elevationList
                }
            ]
        };

        self.options =  {
            scales: {
                xAxes: [{
                    ticks: {
                        max: 20,
                        min: 0,
                        stepSize: 0.5
                    }
                }]
            },

            // Sets the chart to be responsive
            responsive: true,

            ///Boolean - Whether grid lines are shown across the chart
            scaleShowGridLines : false,

            //String - Colour of the grid lines
            scaleGridLineColor : "rgba(0,0,0,.05)",

            //Number - Width of the grid lines
            scaleGridLineWidth : 1,

            //Boolean - Whether the line is curved between points
            bezierCurve : true,

            //Number - Tension of the bezier curve between points
            bezierCurveTension : 0.4,

            //Boolean - Whether to show a dot for each point
            pointDot : false,

            //Number - Radius of each point dot in pixels
            pointDotRadius : 0,

            //Number - Pixel width of point dot stroke
            pointDotStrokeWidth : 0,

            //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
            pointHitDetectionRadius : 20,

            //Boolean - Whether to show a stroke for datasets
            datasetStroke : true,

            //Number - Pixel width of dataset stroke
            datasetStrokeWidth : 2,

            //Boolean - Whether to fill the dataset with a colour
            datasetFill : true,

            // Function - on animation progress
            onAnimationProgress: function(){},

            // Function - on animation complete
            onAnimationComplete: function(){},

            //String - A legend template
            legendTemplate : '<ul class="tc-chart-js-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>'
        };*/
        self.chartConfig = {

            options: {
                //This is the Main Highcharts chart config. Any Highchart options are valid here.
                //will be overriden by values specified below.
                chart: {
                    type: 'spline'
                },
                tooltip: {
                    shared: true
                    /*style: {
                        padding: 10,
                        fontWeight: 'bold'
                    }*/
                }
            },
            //The below properties are watched separately for changes.

            //Series object (optional) - a list of series using normal Highcharts series options.
            series: [{
                name: "HR",
                "color": "red",
                //"dashStyle": "Dot",
                "type": "line",
                data: self.hrList
            },{
                name: "Elevation",
                "type": "areaspline",
                data: self.elevationList
            },{
                name: "Speed",
                "type": "spline",
                data: self.speedList
            }
            ],
            //Title configuration (optional)
            title: {
                text: ''
            },
            //Boolean to control showing loading status on chart (optional)
            //Could be a string if you want to show specific loading text.
            loading: true,

            yAxis: [{ // Primary yAxis
                labels: {
                    format: '{value} BPM',
                    style: {
                        color: "red"
                    }
                },
                title: {
                    text: 'HR',
                    style: {
                        color: "red"
                    }
                },
                opposite: true

            }, { // Secondary yAxis
               // gridLineWidth: 0,
                title: {
                    text: 'Elevation',
                    style: {
                        color: "#9dc7f1"
                    }
                },
                labels: {
                    format: '{value} m',
                    style: {
                        color: "#9dc7f1"
                    }
                }
            }],

            //Configuration for the xAxis (optional). Currently only one x axis can be dynamically controlled.
            //properties currentMin and currentMax provided 2-way binding to the chart's maximum and minimum
            xAxis: {
                currentMin: 0,
                currentMax: self.distanceList[self.distanceList.length-1],
                title: {text: 'km'}
            },
            //Whether to use Highstocks instead of Highcharts (optional). Defaults to false.
            useHighStocks: false,
            //size (optional) if left out the chart will default to size of the div or something sensible.
            /*size: {
                width: 400,
                height: 300
            },*/
            //function (optional)
            func: function (chart) {
                //setup some logic for the chart
            }
        };

        $http.get('/activity/detailStats/' + $routeParams.id + '.json', {
            params: { max: "1000" }
        })
            .success(function (data) {
                self.details = data;

                self.latLonList = [];

                var totalDistance = 0;
                var lastTotalDistanceKms = 0;

                // Collect all the HR, lat lon points for the maps and charts
                for (segmentIdx in data.segments) {
                    var segment = data.segments[segmentIdx];
                    for (trackPointIdx in segment.trackPoints) {
                        var trackPoint = segment.trackPoints[trackPointIdx]
                        // console.log(segment.trackPoints[trackPointIdx].distance);
                        self.latLonList.push([trackPoint.lat,trackPoint.lon]);
                        self.hrList.push(trackPoint.heartRate);
                        if (trackPoint.elevation == null) {
                            trackPoint.elevation = 0;
                        }
                        self.elevationList.push(trackPoint.elevation);
                        self.speedList.push(trackPoint.speedKmh);

                        // Labels for x-axis
                        var distanceLabel = '';
                        totalDistance += Math.round( trackPoint.distance);

                        var totalDistanceKms = Math.round(totalDistance/1000);
                        //console.log("%s, %s", totalDistanceKms, lastTotalDistanceKms);

                        if (totalDistanceKms > lastTotalDistanceKms) {
                            distanceLabel = totalDistanceKms + '';
                            lastTotalDistanceKms = totalDistanceKms;
                        }
                        self.distanceList.push(distanceLabel);
                        //self.labels.push(i++);
                    }
                }

                self.chartConfig.loading = false;

                /*self.chartData.push(self.hrList);
                self.chartData.push(self.elevationList);
                self.chartData.push(self.speedList);*/

                // Zoom to fit
                var bounds = new google.maps.LatLngBounds();
                for (var i = 0; i < self.latLonList.length; i++) {
                    var latlng = new google.maps.LatLng(self.latLonList[i][0], self.latLonList[i][1]);
                    bounds.extend(latlng);
                }
                NgMap.getMap().then(function (map) {
                    map.setCenter(bounds.getCenter());
                    map.fitBounds(bounds);
                });

            })
            .error(function (data, status) {
                console.error('https error', status, data);
            })
            .finally(function () {
            });


        function log() {
            $log.debug("Log at: " + new Date());
        }


        self.selectActivity = selectActivity;
        function selectActivity(activity) {
            $log.debug("Activity details: " + activity.id);
            $location.path("/activity/show/" + activity.id);
        }


    }

})();

