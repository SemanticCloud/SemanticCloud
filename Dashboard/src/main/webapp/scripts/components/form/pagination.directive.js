/* globals $ */
'use strict';

angular.module('dashboardApp')
    .directive('dashboardAppPagination', function() {
        return {
            templateUrl: 'scripts/components/form/pagination.html'
        };
    });
