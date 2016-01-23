'use strict';

angular.module('dashboardApp')
    .controller('ProjectController', function ($scope, Project, ProjectSearch, ParseLinks) {
        $scope.projects = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Project.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.projects = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Project.get({id: id}, function(result) {
                $scope.project = result;
                $('#deleteProjectConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Project.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteProjectConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            ProjectSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.projects = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.project = {name: null, description: null, id: null};
        };
    });
