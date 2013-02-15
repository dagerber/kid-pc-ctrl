'use strict';

/* App Module */

angular.module('kidpcctrl', []).
  config(['$routeProvider', function($routeProvider) {
      $routeProvider.
      when('/info',          {templateUrl: 'partials/info.html',   controller: InfoCtrl}).
      otherwise(              {redirectTo:  '/info'});
}]);
