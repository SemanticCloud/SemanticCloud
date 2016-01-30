'use strict';

angular.module('dashboardApp')
    .factory('SemanticEngineService', function ($http) {
        var baseUrl = 'semantic-engine/classes/';
        return {
            getClass: function (classUri) {
                return $http.get(baseUrl + '/' + classUri).then(function (response) {
                    return response.data;
                });
            }
        };
    });
