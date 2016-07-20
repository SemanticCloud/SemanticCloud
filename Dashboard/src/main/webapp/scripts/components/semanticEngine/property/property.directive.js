'use strict';
angular.module('dashboardApp')
    .directive('seProperty', function (SemanticEngineService, $log) {
        return {
            scope: {
                classUri: '=',
                propertyCondition: '=',
                properties: '='
            },
            templateUrl: 'scripts/components/semanticEngine/property/property.html',
            controller: function ($scope) {
                $scope.operators =[
                    { name:"equal", url:"scripts/components/semanticEngine/condition/simpleValue.html"},
                    { name:"describedBy", url:"scripts/components/semanticEngine/condition/objectValue.html"},
                    { name:"equalTo", url:"scripts/components/semanticEngine/condition/individualValue.html"},
                ];

                $scope.initCondition = function(type, operator){
                    $log.info(type);
                    $scope.propertyCondition.type = type;
                    $scope.propertyCondition.operator = operator;
                };


                $scope.updateOperators = function(){
                    $scope.propertyCondition.uri = $scope.property.uri;
                    //chane localName to Uri
                    SemanticEngineService.getIndividualsInRange($scope.classUri,$scope.property.localName).then(
                        function(data){
                            $scope.individuals = data;
                        });
                    SemanticEngineService.getClassInRange($scope.classUri,$scope.property.localName).then(
                        function(data){
                            $scope.classes = data;
                        });
                    if($scope.property.datatype == null){
                        $scope.operators =[
                            { name:"describedBy", url:"scripts/components/semanticEngine/condition/objectValue.html"},
                            { name:"equalTo", url:"scripts/components/semanticEngine/condition/individualValue.html"},
                        ];
                    }
                    //todo numeric property
                    else {
                        $scope.operators =[
                            { name:"equal", url:"scripts/components/semanticEngine/condition/simpleValue.html"},
                            { name:"greaterThan", url:"scripts/components/semanticEngine/condition/greaterThan.html"},
                            { name:"lessThan", url:"scripts/components/semanticEngine/condition/lessThan.html"}
                        ];
                    }

                }
            }
        }
    });
