'use strict';

angular.module('app.pages.negotiation')
    .factory('SemanticEngineService', function (Restangular) {
        var base = Restangular.all('semantic-engine/');
        return {
            getClass: function (classUri) {
                return base.one('class', classUri).get();
            },
            getClassProperties: function (classUri) {
                return base.one('class', classUri).all('property').getList();
            },
            getSubclasses: function (classUri) {
                return base.one('class', classUri).all('subclass').getList();
            },
            getClassInRange: function (classUri, propertUri) {
                return base.one('class', classUri).one('property', propertUri).all('classes').getList();
            },
            getIndividualsInRange: function (classUri, propertUri) {
                return base.one('class', classUri).one('property', propertUri).all('individuals').getList();
            },
            create: function (conditions) {
                return base.all('condition').post(conditions);
            }
        };
    });
