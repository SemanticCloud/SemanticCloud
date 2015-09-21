angular.module('app').controller('PropertyController', ['$scope', 'SemanticEngineService', function ($scope, SemanticEngineService) {
    'use strict';
    var classOperators = ['equalToIndividual', 'describedWith', 'constrainedBy'];
    var literalOperators = ['equalTo'];
    var intOperator = ['equalTo', 'lessThan', 'greaterThan'];

    $scope.propertyChange = function () {
        //todo use uri
        $scope.operators = SemanticEngineService.getOperators($scope.classUri, $scope.property.localName).$object;
    };

    $scope.operatorChange = function () {
        SemanticEngineService.getValueCondition($scope.classUri, $scope.property.uri, $scope.operator);
    };

    $scope.isInput = function(){
        return $scope.operator === 'equalTo' || $scope.operator === 'lessThan' || $scope.operator === 'greaterThan';
    };


}]);