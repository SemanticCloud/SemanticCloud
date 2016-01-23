'use strict';

angular.module('dashboardApp')
    .controller('CredentialsDetailController', function ($scope, $rootScope, $stateParams, entity, Credentials, User) {
        $scope.credentials = entity;
        $scope.load = function (id) {
            Credentials.get({id: id}, function(result) {
                $scope.credentials = result;
            });
        };
        $rootScope.$on('dashboardApp:credentialsUpdate', function(event, result) {
            $scope.credentials = result;
        });
    });
