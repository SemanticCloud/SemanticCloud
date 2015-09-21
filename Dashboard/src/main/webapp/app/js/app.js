angular.module('app', [
    'ui.router',
    'restangular'
]).config(
    ['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            'use strict';
            $urlRouterProvider
               .otherwise('/');
            $stateProvider
                .state('app', {
                    url: '',
                    templateUrl: 'app.html'
                });
        }
    ]
);
