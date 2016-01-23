'use strict';

angular.module('dashboardApp')
    .controller('ProjectDetailController', function ($scope, $rootScope, $stateParams, entity, Project, User) {
        $scope.project = entity;
        $scope.load = function (id) {
            Project.get({id: id}, function(result) {
                $scope.project = result;
            });
        };
        $rootScope.$on('dashboardApp:projectUpdate', function(event, result) {
            $scope.project = result;
        });
    });
