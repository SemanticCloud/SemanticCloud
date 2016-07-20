'use strict';

angular.module('dashboardApp')
    .controller('NegotiationController', function ($scope, $log, SemanticEngineService) {
        $scope.conditions = [];

        $scope.addCondition = function(){
            $scope.conditions.push({ name: 'Compute', uri: 'Compute', localName:'Compute'});
        };

        $scope.save = function() {
            $log.info($scope.conditions[0]);
            SemanticEngineService.create($scope.conditions);
        };
    });
