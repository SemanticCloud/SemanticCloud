'use strict';

angular.module('dashboardApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('admin', {
                parent: 'home',
                url: 'admin',
                views: {
                    'navbar@': {
                        templateUrl: 'scripts/app/admin/navbar.html',
                        controller: 'NavbarController'
                    }
                },
            });
    });
