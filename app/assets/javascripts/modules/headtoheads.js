var headtoheads = angular.module('rokta.headtoheads', ['rokta.events', 'rokta.players', 'rokta.stats', 'rokta.filters', 'rokta.colours']);

// A factory to create a HeadToHead object used to create the graph series.
headtoheads.service('HeadToHeads', [
function() {
  var service = {
    create: function(players, allColours, headToHeads) {
      var activePlayerNames = _(headToHeads).keys().sortBy();
      var activePlayers = 
        _(players).filter(function (player) { return activePlayerNames.contains(player.name); }).indexBy('name').value();
      var colours = activePlayerNames.map(function(name) {
        var player = activePlayers[name];
        return allColours[player.colour].rgb;
      }).value();
      var playerIndiciesByName = activePlayerNames.reduce(function (playerIndiciesByName, player, idx) {
        playerIndiciesByName[player] = idx;
        return playerIndiciesByName;
      }, {});
      var series = [];
      // Initialise the data
      activePlayerNames.forEach(function(name) {
        var data = [];
        activePlayerNames.forEach(function() { data.push(null) });
        series.push({name: name, data: data, pointPlacement: 'on'});
      });
      // Populate the data
      var max, min;
      activePlayerNames.forEach(function(winner, idx) {
        _(headToHeads[winner]).forIn(function(wins, loser) {
          var losses = headToHeads[loser][winner]
          if (losses) {
            var result = parseFloat((100 * wins / (wins + losses)).toFixed(2));
            max = max ? Math.max(max, result) : result;
            min = min ? Math.min(min, result) : result; 
            series[playerIndiciesByName[loser]].data[idx] = 
              {y: result, winner: winner, wins: wins, loser: loser, losses: losses};
          }
        });
      });
      return {names: activePlayerNames.value(), colours: colours, series: series, max: max, min: min};
    }
  };
  return service;
}]);

headtoheads.directive('roktaHeadtoheads', [
function() {
  return {
    restrict : 'A',
    scope : {
      roktaHeadtoheads : '=',
      roktaTitle : '@'
    },
    link : function($scope, elem, attrs) {
      var drawChart = function() {
        var headToHeads = $scope.roktaHeadtoheads;
        elem.highcharts({
          colors: headToHeads.colours,
          chart: {
            polar: true,
            type: 'line'
          },
          title: {
            text: ""
          },
          xAxis: {
            categories: headToHeads.names,
            tickmarkPlacement: 'on',
            lineWidth: 0
          },
          yAxis: {
            gridLineInterpolation: 'polygon',
            lineWidth: 0,
            startOnTick: false,
            endOnTick: false,
            min: headToHeads.min,
            max: headToHeads.max
          },
          pane: {
            size: '90%'
          },
          tooltip: {
            shared: true,
              pointFormat: 
                '<span style="color:{series.color}">{series.name}: <b>{point.y:,.0f}% ({point.winner}: {point.wins} / {point.loser}: {point.losses})</b><br/>'
          },
          legend: {
            align: 'right',
            verticalAlign: 'top',
            y: 70,
            layout: 'vertical'
          },
          series: headToHeads.series,
          plotOptions: {
            line: {
              connectNulls: true
            }
          }
        });
      };
      $scope.$watch('roktaHeadtoheads', function(roktaHeadToHeads) {
        if (roktaHeadToHeads) {
          drawChart();
        }
      });
    }
  };
}]);

headtoheads.controller('HeadToHeadsCtrl', ['$log', '$scope', 'Events', 'Stats', 'Players', 'Colours', 'HeadToHeads',
function($log, $scope, Events, Stats, Players, Colours, HeadToHeads) {
  Stats.refresh();
  Colours.refresh();
  Events.listenTo($scope, [Stats, Players, Colours], function() {
    $scope.headToHeads = HeadToHeads.create(Players.players, Colours.colourMap, Stats.stats.headToHeads);
  });
}]);
