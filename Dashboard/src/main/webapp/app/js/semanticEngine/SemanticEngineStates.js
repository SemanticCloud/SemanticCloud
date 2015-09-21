angular.module('app').config(
    ['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            'use strict';
            $stateProvider
                .state('app.condition', {
                    url: '/condition',
                    controller: 'ConditionController',
                    templateUrl: 'semanticEngine/condition.html'
                });
        }
    ]
);
