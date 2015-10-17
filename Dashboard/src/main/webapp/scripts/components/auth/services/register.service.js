'use strict';

angular.module('dashboardApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


