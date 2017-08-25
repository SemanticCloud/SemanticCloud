(function () {
    'use strict';
  
    angular.module('app.pages.provisioning', [
    ])
        .config(routeConfig);
  
    /** @ngInject */
    function routeConfig($stateProvider) {
      $stateProvider
          .state('provisioning', {
            url: '/provisioning',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            abstract: true,
            title: 'Provisioning',
            sidebarMeta: {
              icon: 'fa fa-cogs',
              order: 101,
            },
          });
    }
  
  })();