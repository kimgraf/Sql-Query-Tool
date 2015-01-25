class UsersCtrl

	constructor: (@$scope, @$location, @$rootScope, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Users Controller"
		@searchText
		@users = []
		@$scope.userData = {}
		@getAllUsers()

	getRoles: ()

	editUser: (name) ->
        @$log.debug "editUser() #{name}"
        @SqlQueriesService.findByName("/user/findbyname/#{name}")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} user"
                @$rootScope.user = data
                @$location.path("/user/edit")
            ,
            (error) =>
                @$log.error "Unable to get user: #{error}"
            )

	getAllUsers: () ->
		@$log.debug "getAllQueries()"

		@SqlQueriesService.list("/users", "Users")
		.then(
		    (data) =>
		        @$log.debug "Promise returned #{data.length} users"
		        @users = data
		        for user in @users
		            @$scope.userData[user.name] = {'gridOptions': {gridDim: null, data: null}}
		    ,
		    (error) =>
		        @$log.error "Unable to get users: #{error}"
		    )

	createUser: () ->
        @$log.debug "createUser()"
        @$rootScope.userData = null
        @$rootScope.user = null
        @$location.path "/user/new"

	deleteUser: (name) ->
        @$log.debug "deleteUser() #{name}"
        BootstrapDialog.confirm(
            "Do you want to delete #{name} user"
            (result) =>
                if result
                    @SqlQueriesService.deleteByName("/users/deletebyname/#{name}")
                    .then(
                        (data) =>
                            @$log.debug "Promise returned #{data.length} users"
                            @getAllQueries()
                        ,
                        (error) =>
                            @$log.error "Unable to get users: #{error}"
                        )
                else
                    @$log.debug "deleteUser() #{name}"
                )


controllersModule.controller('UsersCtrl', UsersCtrl)