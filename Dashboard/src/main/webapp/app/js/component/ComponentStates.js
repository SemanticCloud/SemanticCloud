
angular.module('app').config(
    ['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            'use strict';
            $stateProvider
                .state('app.component', {
                    url: '/component',
                    controller: 'ComponentController',
                    templateUrl: 'semanticEngine/condition/class.html'
                });
        }
    ]
);
