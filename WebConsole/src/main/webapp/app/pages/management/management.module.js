(function () {
    'use strict';
  
    angular.module('app.pages.management', [
    ])
        .config(routeConfig);
  
    /** @ngInject */
    function routeConfig($stateProvider) {
      $stateProvider
          .state('management', {
            url: '/management',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            abstract: true,
            title: 'Management',
            sidebarMeta: {
              icon: 'fa fa-sliders',
              order: 102,
            },
          });
    }
  
  })();