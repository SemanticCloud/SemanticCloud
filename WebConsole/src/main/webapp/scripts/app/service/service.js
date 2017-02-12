'use strict';

angular.module('WebConsole')
    .config(function ($stateProvider) {
        $stateProvider
            .state('service', {
                parent: 'site',
                url: '/service',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/service/services.html',
                        controller: 'ServicesController'
                    }
                }
            });
    });
