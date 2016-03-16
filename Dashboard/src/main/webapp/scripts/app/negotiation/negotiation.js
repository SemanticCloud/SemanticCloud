'use strict';

angular.module('dashboardApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('negotiation', {
                parent: 'site',
                url: '/negotiation',
                data: {
                    //authorities: ['ROLE_USER'],
                    pageTitle: 'dashboardApp.negotiation.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/negotiation/negotiation.html',
                        controller: 'NegotiationController'
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
