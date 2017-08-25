'use strict';

angular.module('app.pages.negotiation')
    .directive('classCondition', function () {
        return {
            scope: {
                condition: '='
            },
            templateUrl: 'app/pages/negotiation/class/class.html',
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
