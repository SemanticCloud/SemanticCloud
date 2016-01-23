'use strict';

angular.module('dashboardApp').controller('CredentialsDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Credentials', 'User',
        function($scope, $stateParams, $modalInstance, entity, Credentials, User) {

        $scope.credentials = entity;
        $scope.users = User.query();
        $scope.load = function(id) {
            Credentials.get({id : id}, function(result) {
                $scope.credentials = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('dashboardApp:credentialsUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.credentials.id != null) {
                Credentials.update($scope.credentials, onSaveFinished);
            } else {
                Credentials.save($scope.credentials, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
