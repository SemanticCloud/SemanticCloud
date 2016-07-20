'use strict';

angular.module('dashboardApp')
    .directive('seClass', function () {
        return {
            scope: {
                condition: '='
            },
            templateUrl: 'scripts/components/semanticEngine/class/class.html',
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
