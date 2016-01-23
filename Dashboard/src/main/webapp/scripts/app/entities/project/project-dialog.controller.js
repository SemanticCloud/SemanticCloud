'use strict';

angular.module('dashboardApp').controller('ProjectDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Project', 'User',
        function($scope, $stateParams, $modalInstance, entity, Project, User) {

        $scope.project = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            Project.get({id : id}, function(result) {
                $scope.project = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('dashboardApp:projectUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.project.id != null) {
                Project.update($scope.project, onSaveFinished);
            } else {
                Project.save($scope.project, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
