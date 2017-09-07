(function () {
    'use strict';
  
    angular.module('app.pages.resource', [
      'app.pages.resource.compute'
    ])
        .config(routeConfig);
  
    /** @ngInject */
    function routeConfig($stateProvider) {
      $stateProvider
          .state('resource', {
            url: '/resource',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            abstract: true,
            title: 'Resources',
            sidebarMeta: {
              icon: 'fa fa-sliders',
              order: 102,
            },
          })
          .state('resource.computing', {
            url: '/computing',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            //abstract: true,
            title: 'Computing Resources',
            sidebarMeta: {
              icon: 'fa fa-sliders',
              order: 102,
            },
          })
          .state('resource.storage', {
            url: '/storage',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            //abstract: true,
            title: 'Storage Resources',
            sidebarMeta: {
              icon: 'fa fa-sliders',
              order: 102,
            },
          })
          .state('resource.networking', {
            url: '/networking',
            template : '<ui-view  autoscroll="true" autoscroll-body-top></ui-view>',
            //abstract: true,
            title: 'Networking Resources',
            sidebarMeta: {
              icon: 'fa fa-sliders',
              order: 102,
            },
          });
    }
  
  })();