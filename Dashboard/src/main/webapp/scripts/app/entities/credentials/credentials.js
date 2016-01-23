'use strict';

angular.module('dashboardApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('credentials', {
                parent: 'entity',
                url: '/credentialss',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'dashboardApp.credentials.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/credentials/credentialss.html',
                        controller: 'CredentialsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('credentials');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('credentials.detail', {
                parent: 'entity',
                url: '/credentials/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'dashboardApp.credentials.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/credentials/credentials-detail.html',
                        controller: 'CredentialsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('credentials');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Credentials', function($stateParams, Credentials) {
                        return Credentials.get({id : $stateParams.id});
                    }]
                }
            })
            .state('credentials.new', {
                parent: 'credentials',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/credentials/credentials-dialog.html',
                        controller: 'CredentialsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {identity: null, credential: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('credentials', null, { reload: true });
                    }, function() {
                        $state.go('credentials');
                    })
                }]
            })
            .state('credentials.edit', {
                parent: 'credentials',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/credentials/credentials-dialog.html',
                        controller: 'CredentialsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Credentials', function(Credentials) {
                                return Credentials.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('credentials', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
