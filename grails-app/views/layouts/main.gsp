<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <link rel="stylesheet" href="http://ajax.googleapis.com/ajax/libs/angular_material/1.1.0-rc2/angular-material.min.css">
    <asset:stylesheet href="webjars/font-awesome/4.6.3/css/font-awesome.css"/>

    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

    <asset:stylesheet href="application.css"/>
    <asset:stylesheet href="angular-chart.min.css"/>

    <g:layoutHead/>
</head>
<body ng-app="trails" layout="column" ng-cloak="">

<!-- Container #1 (see wireframe) -->
<md-toolbar class="md-hue-2">
    <div class="md-toolbar-tools">
        %{--Icons: https://design.google.com/icons/--}%
        <md-button class="md-icon-button" aria-label="Settings" ng-disabled="true">
            %{--<md-icon md-svg-icon="img/icons/menu.svg"></md-icon>--}%
        </md-button>

        <h2>
            <span><a href="/#/">Trails</a></span>
        </h2>
        <span flex></span>
        <md-button class="md-icon-button" aria-label="Favorite" ng-click="act.log()">
            <md-icon md-font-set="material-icons" style="color: greenyellow;">pets</md-icon>
        </md-button>
        <md-button class="md-icon-button" aria-label="Favorite" ng-click="act.log()">
            <md-icon md-font-set="material-icons">face</md-icon>
            %{--<md-icon md-svg-icon="img/icons/favorite.svg" style="color: greenyellow;"></md-icon>--}%
        </md-button>
        %{-- <md-button class="md-icon-button" aria-label="More">
        <md-icon md-svg-icon="img/icons/more_vert.svg"></md-icon>
    </md-button>--}%
    </div>
</md-toolbar>

<!-- Container #2 -->
<div flex layout="row">
    <!-- Wireframe Container #3 -->
    <md-sidenav md-is-locked-open="true" class="md-whiteframe-4dp">
        %{--<md-list>
        <md-list-item ng-repeat="u in act.users">
            <md-button ng-click="act.selectUser(u)" ng-class="{'selected' : u === act.selected }">
                <md-icon md-svg-icon="{{u.avatar}}" class="avatar"></md-icon>
                {{u.name}}
            </md-button>
            <md-divider ng-if="!$last"></md-divider>
        </md-list-item>
    </md-list>
        --}%
        <md-list ng-controller="ActivityController as act" >
            <md-list-item ng-repeat="activity in act.recentActivities">
                <md-button ng-click="act.selectActivity(activity)" ng-class="{'selected' : activity === act.selected }">
                    <md-icon %{--md-svg-icon="{{u.avatar}}"--}% class="avatar"></md-icon>
                    {{activity.name}} {{activity.typeName}}
                </md-button>
                <md-divider ng-if="!$last"></md-divider>
            </md-list-item>
        </md-list>
    </md-sidenav>

    <!-- Wireframe Container #4 -->
    <md-content flex id="content">

        <div ng-view></div>

    </md-content>
</div>
<!-- apiCheck is used by formly to validate its api -->
<script src="http://rawgit.com/kentcdodds/api-check/latest/dist/api-check.js"></script>

<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular.min.js"></script>

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular-route.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular-animate.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular-aria.js"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.5.6/angular-messages.js"></script>

<script src="http://cdn.jsdelivr.net/alasql/0.3/alasql.min.js"></script>

%{--<asset:javascript src="Chart.js"/>--}%

%{--<script src="//cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.6/Chart.bundle.min.js"></script>--}%

<script src="http://code.highcharts.com/highcharts.src.js"></script>

<script src="//ajax.googleapis.com/ajax/libs/angular_material/1.1.0-rc2/angular-material.min.js"></script>


<asset:javascript src="application.js"/>

</body>
</html>
