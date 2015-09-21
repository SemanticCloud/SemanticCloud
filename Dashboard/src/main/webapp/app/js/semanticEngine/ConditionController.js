angular.module('app').controller('ConditionController', ['$scope', 'SemanticEngineService', function($scope, SemanticEngineService) {
    'use strict';
    $scope.classUri ='Compute';
    $scope.classInfo= {};
    $scope.properties = [];

    $scope.addProperty = function(){
        var property = {};
        $scope.properties.push(property);
        $log.info('add property');
    };
    $scope.removeProperty = function(index){
        var property = {};
        $scope.properties.splice(index,1);
        $log.info('remove property'+index);
    };

    SemanticEngineService.getClass($scope.classUri).then(function(classInfo){
        $scope.classInfo = classInfo;
    },function(){
        $scope.classInfo = {};
    });


}]);