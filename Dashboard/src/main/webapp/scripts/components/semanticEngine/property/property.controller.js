'use strict';

angular.module('dashboardApp')
    .controller('PropertyController', function ($scope) {
        $scope.property = null;
        $scope.operator = null;

        $scope.operators =[
            { name:"equal", condition:"scripts/components/semanticEngine/condition/simpleValue.html"},
            { name:"constrained", condition:"scripts/components/semanticEngine/condition/objectValue.html"},
            { name:"equalTo", condition:"scripts/components/semanticEngine/condition/individualValue.html"},
        ];
    });
