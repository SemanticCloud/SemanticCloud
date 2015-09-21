angular.module('app').config(
    ['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {
            'use strict';
            $stateProvider
                .state('app.project', {
                    url: '/project',
                    abstract: true
                })
                .state('app.project.list', {
                    url: '/list',
                    templateUrl: 'project/list.html'
                });
        }
    ]
);