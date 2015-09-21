angular.module('app').controller('ComponentController', ['$scope', 'SemanticEngineService', '$log', function($scope, SemanticEngineService, $log) {
    'use strict';
    $scope.classUri ='http://semantic-cloud.org/cloud#Compute';
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

    SemanticEngineService.propertyList($scope.classUri).then(function(classInfo){
        $scope.classInfo = classInfo;
    },function(){
        $scope.classInfo = {};
    });


}]);