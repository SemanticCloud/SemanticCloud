'use strict';

angular.module('dashboardApp')
    .controller('PropertyController', function ($scope) {

        $scope.components = [];

        $scope.addComponent = function(){
            $scope.components.push({});
        };
    });
