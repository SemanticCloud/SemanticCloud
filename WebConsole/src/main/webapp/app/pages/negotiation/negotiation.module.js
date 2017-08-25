(function () {
    'use strict';
  
    angular.module('app.pages.negotiation', [
    ])
        .config(routeConfig);
  
    /** @ngInject */
    function routeConfig($stateProvider) {
      $stateProvider
          .state('negotiation', {
            url: '/negotiation',
            title: 'Negotiation',
            templateUrl: 'app/pages/negotiation/negotiation.html',
            controller: "NegotiationController",
            //controllerAs: "detailCtrl",
            sidebarMeta: {
              icon: 'fa fa-cogs',
              order: 102,
            },
          });
    }
  
  })();