<%@ page import="trails.service.StatisticsService" %>


<div>


    <!--<md-grid-list md-cols="5" md-gutter="1em" md-row-height="4:3" ng-repeat="statRow in overallStats.allStats.stats.allTypes">
         <md-grid-tile>{{statRow.distance/1000 | number:2 }}</md-grid-tile>
         <md-grid-tile>{{statRow.totalTimeFormatted}}</md-grid-tile>
     </md-grid-list>-->

    <!--<pre>{{ overallStats.allStats | json }}</pre>-->

    %{--TODO: Need to display timezone local time since the GPX file is in UTC--}%
    <md-list ng-cloak>
        <md-subheader class="md-no-sticky">Overall stats</md-subheader>
        %{-- <md-list-item ng-repeat="statRow in overallStats.allStats.stats">
            <p>*{{ statRow }}*</p>
        </md-list-item>
--}%
        %{--<p>{{ overallStats.allStats.stats.forever.allTypes }}</p>--}%


        <md-list-item>
            <table class="table table-bordered table-hover table-condensed">
                <thead>
                <tr>
                    <td></td>
                    <td>Activities</td>
                    <td>Distance</td>
                    <td>Duration</td>
                    <td>Avg Distance</td>
                    <td>Avg Duration</td>
                    <td>Total Suffer</td>
                    <td>Avg Suffer</td>
                    <td>Avg HR</td>
                    <td>Avg Speed/pace</td>
                </tr>
                </thead>
                %{--TODO: Since this is a GSP we can generate and order the statistics based on user prefs!--}%
                <tr><td colspan="10">Since joining</td></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.forever.allTypes" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.forever.Running" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.forever.Cycling" ></tr>
                <tr ng-show="overallStats.allStats.stats.forever.allTypes.totalActivities == 0">
                    <td></td>
                    <td colspan="10">No activities</td>
                </tr>

                <tr><td colspan="10">Year</td></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.yearStats.allTypes" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.yearStats.Running" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.yearStats.Cycling" ></tr>
                <tr ng-show="overallStats.allStats.stats.yearStats.allTypes.totalActivities == 0">
                    <td></td>
                    <td colspan="10">No activities</td>
                </tr>

                <tr><td colspan="10">Month</td></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.monthStats.allTypes" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.monthStats.Running" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.monthStats.Cycling" ></tr>
                <tr ng-show="overallStats.allStats.stats.monthStats.allTypes.totalActivities == 0">
                    <td></td>
                    <td colspan="10">No activities  for the last month</td>
                </tr>

                <tr><td colspan="10">Week</td></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.weekStats.allTypes" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.weekStats.Running" ></tr>
                <tr overall-stats-row stats="overallStats.allStats.stats.weekStats.Cycling" ></tr>
                <tr ng-show="overallStats.allStats.stats.weekStats.allTypes.totalActivities == 0">
                    <td></td>
                    <td colspan="10">No activities for the last week</td>
                </tr>
            </table>
        </md-list-item>

        <md-divider></md-divider>

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
        <md-subheader class="md-no-sticky">Secondary Menus</md-subheader>
    </md-list>

    <!--<md-list>
        <md-list-item ng-repeat="statRow in overallStats.allStats.stats.allTypes">
            <span>{{statRow.distance/1000 | number:2 }}</span>
            <span>{{statRow.totalDurationSeconds}}</span>
        </md-list-item>

    </md-list>-->

    <!--<md-table-container>
        <table md-table md-row-select multiple ng-model="row" md-progress="promise">
            <thead md-head md-order="query.order" md-on-reorder="getDesserts">
            <tr md-row>
                <th md-column md-numeric>Distance</th>
                <th md-column md-numeric>totalDurationSeconds</th>
            </tr>
            </thead>
            <tbody md-body>
            <tr md-row md-select="dessert" md-select-id="name" md-auto-select ng-repeat="statRow in overallStats.allStats.stats.allTypes">
                <td md-cell>{{statRow.distance}}</td>
                <td md-cell>{{statRow.totalDurationSeconds}}</td>
            </tr>
            </tbody>
        </table>
    </md-table-container>-->

</div>

