'use strict';

angular.module('dashboardApp')
    .controller('EngineController', function ($scope, SemanticEngineService) {
        $scope.classUri = 'http://semantic-cloud.org/cloud#Compute';
        $scope.classInfo = SemanticEngineService.getClass($scope.classUri);
    });
