'use strict';

angular.module('dashboardApp')
    .factory('SemanticEngineService', function (Restangular) {
        var base = Restangular.all('semantic-engine/');
        return {
            getClass: function (classUri) {
                return base.one('class', classUri).get();
            },
            getClassProperties: function (classUri) {
                return base.one('class', classUri).all('property').getList();
            },
            getClassInRange: function (classUri, propertUri) {
                return base.one('class', classUri).one('property', propertUri).all('classes').getList();
            },
            getIndividualsInRange: function (classUri, propertUri) {
                return base.one('class', classUri).one('property', propertUri).all('individuals').getList();
            }
        };
    });
