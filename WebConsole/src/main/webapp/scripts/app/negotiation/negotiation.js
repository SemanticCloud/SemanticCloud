'use strict';

angular.module('WebConsole')
    .config(function ($stateProvider) {
        $stateProvider
            .state('negotiation', {
                parent: 'site',
                url: '/negotiation',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/negotiation/negotiation.html',
                        controller: 'NegotiationController'
                    }
                }
            });
    });
