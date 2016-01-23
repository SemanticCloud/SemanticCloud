'use strict';

angular.module('dashboardApp')
    .factory('ProjectSearch', function ($resource) {
        return $resource('api/_search/projects/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
