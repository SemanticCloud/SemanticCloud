'use strict';

angular.module('WebConsole')
    .directive('classCondition', function () {
        return {
            scope: {
                condition: '='
            },
            templateUrl: 'scripts/app/negotiation/class/class.html',
            controller: function ($scope, SemanticEngineService) {
                SemanticEngineService.getClassProperties($scope.condition.localName).then(function(properties){
                    $scope.properties = properties;
                });
                $scope.condition.propertyConditions = [];

                $scope.addPropertyCondition = function(){
                    $scope.condition.propertyConditions.push({});
                };
            }
        }
    });
