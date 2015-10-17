'use strict';

angular.module('dashboardApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
