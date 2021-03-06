var interactive = angular.module('rokta.common.interactive', ['rokta.common.routing', 'rokta.common.auth']);

interactive.service('MessageQueue', ['$timeout', '$log', 'ROUTES', function($timeout, $log, ROUTES) {
  var seconds = function(time) {
    return 1000 * time;
  };
  $log.info("Attempting to connect to the websocket.");
  var ws = new WebSocket(ROUTES.ws);
  ws.onerror = function(err) {
    $log.error("The websocket errored " + angular.toJson(err));
  }
  var reopen = function(message) {
    $log.warn(message);
    $timeout(function() {
    var newws = new WebSocket(ROUTES.ws);
    newws.onopen = ws.onopen;
    newws.onerror = ws.onerror;
    newws.onmessage = ws.onmessage;
    newws.onclose = ws.onclose;
    ws = newws;
    }, seconds(10));
  }
  ws.onclose = function() {
    reopen("The websocket closed");
  };
  ws.onerror = function() {
    reopen("The websocket errored");
  }
  $timeout(function() {
    $log.info("After 1 second the websocket readyState is " + ws.readyState);
    if (ws.readyState != WebSocket.OPEN) {
      $log.warn("The websocket connection timed out. Closing.")
      ws.close();
    }
  }, seconds(1));
  var keepAlive = function() {
    $timeout(function() {
      if (ws.readyState == WebSocket.OPEN) {
        //$log.debug("Sending keep alive ping.")
        ws.send('{ "type": "ping" }');
      }
      else {
        reopen("The web socket was not opened. Reopening.");
      }
      keepAlive();
    }, seconds(10))
  }
  keepAlive();
  return {
    onOpen: function(listener) {
      if (ws.readyState == WebSocket.OPEN) {
        $log.info("The websocket was already open so calling callback function straight away.")
        listener();
      }
      else {
        ws.onopen = function() {
          $log.info("The websocket opened");
          listener();
        }
      }
    },
    onMessage: function(listener) {
      ws.onmessage = listener;
    },
    send: function(message) {
      ws.send(message);
    }
  };
}]);

interactive.service('Interactive', ['$log', '$rootScope', 'MessageQueue', 'AUTH',
function($log, $rootScope, MessageQueue, AUTH) {
  var service = {
    onStateChange: function(listener) {
      $log.info("Listening to game state changes.");
      MessageQueue.onOpen(function() {
        $log.info("Requesting current state.");
        service.send({type: "sendCurrentState"});
      });
      MessageQueue.onMessage(function(message) {
        var state = angular.fromJson(message.data).state;
        _(state).assign({inProgress: state.type != "notStarted"});
        $log.info("Received state: " + angular.toJson(state));
        $rootScope.$apply(function() { listener(state); });
      });
    },
    send: function(message) {
      var data = angular.toJson(message);
      $log.info("Sending " + data);
      MessageQueue.send(data);
    },
    instigate: function() {
      service.send({type: "instigator", instigator: AUTH.name});
    },
    join: function() {
      service.send({type: "newPlayer", player: AUTH.name});
    },
    start: function() {
      service.send({type: "startGame"});
    },
    play: function(hand) {
      service.send({type: "handPlayed", player: AUTH.name, hand: hand});
    },
    accept: function() {
      service.send({type: "acceptGame"});
    },
    undo: function() {
      service.send({type: "back"});
    },
    cancel: function() {
      service.send({type: "cancel"});
    }
  };
  return service;
}]);
