'use strict';

angular.module('dashboardApp')
    .controller('CredentialsController', function ($scope, Credentials, CredentialsSearch, ParseLinks) {
        $scope.credentialss = [];
        $scope.page = 0;
        $scope.loadAll = function() {
            Credentials.query({page: $scope.page, size: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.credentialss = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Credentials.get({id: id}, function(result) {
                $scope.credentials = result;
                $('#deleteCredentialsConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Credentials.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteCredentialsConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.search = function () {
            CredentialsSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.credentialss = result;
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
            $scope.credentials = {identity: null, credential: null, id: null};
        };
    });
