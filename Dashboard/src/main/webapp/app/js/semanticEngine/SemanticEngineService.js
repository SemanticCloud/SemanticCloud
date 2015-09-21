angular.module('app').factory('SemanticEngineService', ['Restangular', function SemanticEngineServiceFactory(Restangular) {
    'use strict';
    //var base = Restangular.oneUrl('class','http://localhost:8020');
    var base = Restangular.oneUrl('class');
    var service = {

        propertyList: function (classUri) {
            return Restangular.oneUrl('samples').one('Cloud.json').get({
                classUri: classUri
            });
        },
        getValueCondition: function (classUri, propertyUri, operator) {
            return Restangular.oneUrl('constraints').one('getValueCondition.json').get({
                conditionId: 1,
                classUri: classUri,
                propertyUri: propertyUri,
                operator: operator
            });
        },
        getClass: function(classUri){
            return base.one(classUri).get();
        },
        getOperators: function(classUri, propertyUri){
            return base.one(classUri).all('property').all(propertyUri).all('operators').getList();
        }
    };

    return service;
}]);