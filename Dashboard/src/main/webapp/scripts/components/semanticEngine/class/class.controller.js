'use strict';

angular.module('dashboardApp')
    .controller('ClassController', function ($scope) {

        $scope.properties = ['a','b'];
        $scope.propertyConditions = [];

        $scope.addPropertyCondition = function(){
            $scope.propertyConditions.push({});
        };
    });
