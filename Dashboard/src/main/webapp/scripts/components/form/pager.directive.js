/* globals $ */
'use strict';

angular.module('dashboardApp')
    .directive('dashboardAppPager', function() {
        return {
            templateUrl: 'scripts/components/form/pager.html'
        };
    });
