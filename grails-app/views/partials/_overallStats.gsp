<%@ page import="trails.service.StatisticsService" %>
<tr ng-show="stats.displayText != null">
    <td class="text-right">{{stats.displayText}}</td>
    <td>{{stats.totalActivities}}</td>
    <td>{{stats.distance / 1000 | number:2 }}</td>
    <td>{{stats.totalDurationFormatted}}</td>
    <td>{{stats.averageDistance / 1000 | number:2}}</td>
    <td>{{stats.averageDurationFormatted}}</td>
    <td>{{stats.sufferScore}}</td>
    <td>{{stats.averageSufferScore}}</td>
    <td>{{stats.averageHR}}</td>
    <td>{{stats.averageSpeed}} - {{stats.averagePaceFormatted}}</td>
</tr>
