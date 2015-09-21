//angular.module('app').factory('ProjectService', ['Restangular', function (Restangular) {
//    'use strict';
//    var service = {
//        listProjects: function () {
//            return Restangular.all('projects').getList();
//        },
//        getProject: function (projectId) {
//            return Restangular.one('projects',projectId);
//        },
//        createProject: function (project) {
//            return Restangular.all('projects').post(project);
//        },
//        deleteProject: function (project) {
//            return project.remove();
//        },
//        updateProject: function(project){
//            return project.put();
//        }
//    };
//
//    return service;
//}]);