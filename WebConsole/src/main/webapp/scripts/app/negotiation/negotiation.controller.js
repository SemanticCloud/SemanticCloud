'use strict';

angular.module('WebConsole')
    .controller('NegotiationController', function ($scope, $log, SemanticEngineService) {
        $scope.condition = {
            name: 'Service', localName: 'Service',
            namespace: 'http://semantic-cloud.org/Cloud#',
            uri: 'http://semantic-cloud.org/Cloud#Service'
        };
        $scope.data = null;

        $scope.addCondition = function () {
        };

        $scope.save = function () {
            $log.info($scope.condition);
            SemanticEngineService.create($scope.condition).then(
                function(data){
                    $log.info(data);
                    $scope.data = data;
                }
            );
        };
//        Principal.identity().then(function(account) {
//            $scope.account = account;
//            $scope.isAuthenticated = Principal.isAuthenticated;
//        });
    })
;
