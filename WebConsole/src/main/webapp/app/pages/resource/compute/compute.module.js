(function () {
    'use strict';
  
    angular.module('app.pages.resource.compute', [
    ])
        .config(routeConfig);
  
    /** @ngInject */
    function routeConfig($stateProvider) {
      $stateProvider
          .state('resource.compute', {
            url: '/compute',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            title: 'Compute',
            sidebarMeta: {
              icon: 'fa fa-sliders',
              order: 102,
            },
          });
    }
  
  })();