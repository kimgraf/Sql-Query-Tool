
dependencies = [
    'ngRoute',
    'ui.bootstrap',
#    'ui.grid',
    'sqlQueriesApp.filters',
    'sqlQueriesApp.services',
    'sqlQueriesApp.controllers',
    'sqlQueriesApp.directives',
    'sqlQueriesApp.common',
    'sqlQueriesApp.routeConfig',
    'angularFileUpload',
    'ngSanitize',
    'checklist-model',
#    'ngGrid',
]


app = angular.module('sqlQueriesApp', dependencies)

angular.module('sqlQueriesApp.routeConfig', ['ngRoute'])
    .config ($routeProvider) ->
        $routeProvider
            .when('/', {
                controller : 'SqlQueriesCrtl',
                templateUrl: '/templates/templates.QueryList'
#                templateUrl: '/assets/partials/jdbctemplates/query_list.html'
            })
            .when('/sqlquery/new', {
                controller : 'SqlQueryCtrl',
                templateUrl: '/assets/partials/jdbctemplates/query.html'
            })
            .when('/sqlquery/edit', {
                controller : 'SqlQueryCtrl',
                templateUrl: '/assets/partials/jdbctemplates/query.html'
            })
            .when('/users', {
                controller : 'UsersCtrl',
                templateUrl: '/templates/user.UserList'
            })
            .when('/user/new', {
                controller : 'UsersCtrl',
                templateUrl: '/templates/user.User'
            })
            .when('/user/edit', {
                controller : 'UsersCtrl',
                templateUrl: '/templates/user.User'
            })
            .when('/drivers', {
                controller : 'DriversCrtl',
                templateUrl: '/assets/partials/jdbctemplates/drivers_list.html'
            })
            .when('/drivers/new', {
                controller : 'DriverCtrl',
                templateUrl: '/assets/partials/jdbctemplates/driver.html'
             })
            .when('/drivers/edit', {
                controller : 'DriverCtrl',
                templateUrl: '/assets/partials/jdbctemplates/driver.html'
             })
            .when('/databases', {
                controller : 'DatabasesCrtl',
                templateUrl: '/assets/partials/jdbctemplates/databases_list.html'
            })
            .when('/databases/new', {
                controller : 'DatabasesCrtl',
                templateUrl: '/assets/partials/jdbctemplates/database.html'
            })
            .when('/databases/edit', {
                controller : 'DatabasesCrtl',
                templateUrl: '/assets/partials/jdbctemplates/database.html'
            })
            .otherwise({redirectTo: '/'})

@commonModule = angular.module('sqlQueriesApp.common', [])
@controllersModule = angular.module('sqlQueriesApp.controllers', [])
@servicesModule = angular.module('sqlQueriesApp.services', [])
@modelsModule = angular.module('sqlQueriesApp.models', [])
@directivesModule = angular.module('sqlQueriesApp.directives', [])
@filtersModule = angular.module('sqlQueriesApp.filters', [])
@modalInstance = null
