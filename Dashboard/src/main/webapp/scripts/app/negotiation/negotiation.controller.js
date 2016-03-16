'use strict';

angular.module('dashboardApp')
    .controller('NegotiationController', function ($scope) {
        $scope.components = [];

        $scope.addComponent = function(){
            $scope.components.push({ name: 'Component'});
        };
    });
