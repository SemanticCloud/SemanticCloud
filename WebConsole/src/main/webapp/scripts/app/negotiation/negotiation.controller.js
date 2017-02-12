'use strict';

angular.module('WebConsole')
    .controller('NegotiationController', function ($scope) {
        $scope.condition = {
            name: 'Service', localName: 'Service',
            namespace: 'http://semantic-cloud.org/Cloud#',
            uri: 'http://semantic-cloud.org/Cloud#Service'
        };

        $scope.addCondition = function () {
        };

        $scope.save = function () {
            $log.info($scope.conditions[0]);
            SemanticEngineService.create($scope.conditions);
        };
//        Principal.identity().then(function(account) {
//            $scope.account = account;
//            $scope.isAuthenticated = Principal.isAuthenticated;
//        });
    })
;
