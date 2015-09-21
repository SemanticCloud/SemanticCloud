module.exports = function (lineman) {
    return {
        js: {
            vendor: [
                'vendor/components/angular/angular.min.js',
                'vendor/components/angular-bootstrap/ui-bootstrap.min.js',
                'vendor/components/angular-ui-router/release/angular-ui-router.min.js',
                'vendor/components/restangular/dist/restangular.min.js',
                'vendor/components/lodash/lodash.min.js',
                'vendor/js/**/*.js'
            ],
            app: [
                'app/js/app.js',
                'app/js/**/*.js'
            ]
        },
        css: {
            vendor: [
                'vendor/components/bootstrap/dist/css/bootstrap.min.css',
                'vendor/components/AdminLTE/dist/css/AdminLTE.min.css',
                'vendor/components/AdminLTE/dist/css/skins/skin-green.min.css',
                'vendor/components/components-font-awesome/css/font-awesome.min.css',
                'vendor/css/**/*.css'
            ],
            app: [
                'app/css/**/*.css'
            ]
        },
        webfonts: {
            root : 'fonts',
            vendor : [
                'vendor/components/components-font-awesome/fonts/**/*',
                'vendor/components/bootstrap/fonts/**/*'
            ]
        }
    };
};
