'use strict';

angular.module('dashboardApp')
    .controller('NegotiationController', function ($scope, $log) {
        $scope.conditions = [];

        $scope.addCondition = function(){
            $scope.conditions.push({ name: 'Compute', classUri: "Compute"});
        };

        $scope.save = function() {
            $log.info($scope.conditions[0]);
        };
    });
