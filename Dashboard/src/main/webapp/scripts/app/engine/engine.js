'use strict';

angular.module('dashboardApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('engine', {
                parent: 'site',
                url: '/engine',
                data: {
                    //authorities: ['ROLE_USER'],
                    pageTitle: 'dashboardApp.engine.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/engine/engine.html',
                        controller: 'EngineController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        //$translatePartialLoader.addPart('engine');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
