'use strict';

angular.module('WebConsole')
    .config(function ($stateProvider) {
        $stateProvider
            .state('404', {
                parent: 'site',
                url: '/404',
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/error/404.html'
                    }
                }
            })
            .state('500', {
                parent: 'site',
                url: '/500',
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/error/500.html'
                    }
                }
            });
    });
