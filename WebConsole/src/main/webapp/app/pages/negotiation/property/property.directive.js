'use strict';
angular.module('app.pages.negotiation')
    .directive('seProperty', function (SemanticEngineService, $log) {
        return {
            scope: {
                classUri: '=',
                propertyCondition: '=',
                properties: '='
            },
            templateUrl: 'app/pages/negotiation/property/property.html',
            controller: function ($scope) {
                $scope.operators =[
                    { name:"equal", url:"scripts/app/negotiation/condition/simpleValue.html"},
                    { name:"describedBy", url:"scripts/app/negotiation/condition/objectValue.html"},
                    { name:"equalTo", url:"scripts/app/negotiation/condition/individualValue.html"},
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
                            { name:"describedBy", url:"scripts/app/negotiation/condition/objectValue.html"},
                            { name:"equalTo", url:"scripts/app/negotiation/condition/individualValue.html"},
                        ];
                    }
                    //todo numeric property
                    else {
                        $scope.operators =[
                            { name:"equal", url:"scripts/app/negotiation/condition/simpleValue.html"},
                            { name:"greaterThan", url:"scripts/app/negotiation/condition/greaterThan.html"},
                            { name:"lessThan", url:"scripts/app/negotiation/condition/lessThan.html"}
                        ];
                    }

                }
            }
        }
    });
