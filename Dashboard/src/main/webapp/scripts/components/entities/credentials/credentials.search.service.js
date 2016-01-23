'use strict';

angular.module('dashboardApp')
    .factory('CredentialsSearch', function ($resource) {
        return $resource('api/_search/credentialss/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
