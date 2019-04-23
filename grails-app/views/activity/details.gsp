<%@ page import="trails.service.StatisticsService" %>
%{--
<canvas id="line" class="chart chart-line" chart-data="data"
chart-labels="labels" chart-series="series" chart-options="options"
chart-dataset-override="datasetOverride" chart-click="onClick"
</canvas>--}%

<div class="container-fluid">
    <div class="row">
        <div class="col-lg-8">
            <div map-lazy-load="https://maps.google.com/maps/api/js"
                 map-lazy-load-params="{{actDetails.googleMapsUrl}}">
                <ng-map center="{{actDetails.latLonList[actDetails.latLonList.length/2]}}" map-type-id="TERRAIN">
                    <shape name="polyline"
                           path="{{actDetails.latLonList}}"
                           geodesic="true"
                           stroke-color="#FF0000"
                           stroke-opacity="1.0"
                           stroke-weight="2">
                    </shape>
                </ng-map>
            </div>
        </div>

        <div class="col-lg-4">
            <md-list ng-cloak>
                <md-subheader class="md-no-sticky">Details 2</md-subheader>
                %{-- <md-list-item ng-repeat="statRow in overallStats.allStats.stats">
                    <p>*{{ statRow }}*</p>
                </md-list-item>
        --}%
                %{--<p>{{ overallStats.allStats.stats.forever.allTypes }}</p>--}%

                <md-list-item>
                    {{actDetails.details.activityType}} - {{actDetails.details.name}}
                </md-list-item>

                <md-list-item>
                    <table class="table table-bordered table-hover table-condensed">
                        <thead>
                        <tr>
                            <td></td>
                            <td>Avg</td>
                            <td>Max</td>
                        </tr>
                        </thead>
                        <tr>
                            <td>Speed</td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>HR</td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Cadence</td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Calories</td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Total Time</td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Moving Time</td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>Suffer</td>
                            <td></td>
                            <td></td>
                        </tr>
                    </table>
                </md-list-item>

                %{--<md-divider></md-divider>

                <md-subheader class="md-no-sticky">Secondary Buttons</md-subheader>
                <md-list-item class="secondary-button-padding">
                    <p>Clicking the button to the right will fire the secondary action</p>
                    <md-button class="md-secondary" ng-click="doSecondaryAction($event)">More Info</md-button>
                </md-list-item>
                <md-list-item class="secondary-button-padding" ng-click="doPrimaryAction($event)">
                    <p>Click anywhere to fire the primary action, or the button to fire the secondary</p>
                    <md-button class="md-secondary" ng-click="doSecondaryAction($event)">More Info</md-button>
                </md-list-item>
                <md-divider></md-divider>
                <md-subheader class="md-no-sticky">Secondary Menus</md-subheader>--}%
            </md-list>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-12">
            %{--https://github.com/pablojim/highcharts-ng
            http://jsfiddle.net/gh/get/jquery/1.9.1/highslide-software/highcharts.com/tree/master/samples/highcharts/demo/combo-multi-axes/
            http://pablojim.github.io/highcharts-ng/examples/example.html
            http://jsfiddle.net/pablojim/7cAq3/--}%

            <highchart id="chart1" config="actDetails.chartConfig"></highchart>

        </div>
    </div>
</div>


