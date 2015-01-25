"use strick"
class UserCtrl

    constructor: (@$log, @$rootScope, @$location, @SqlQueriesService) ->
        @$log.debug "constructing UserCtrl"
        @errorMsg = ""
        @user = {}
        @isEdit = false
        @viewrole = true
        if @$rootScope.user
            @user = @$rootScope.user
            @isEdit = true
        if not @user.roles
            @user.roles = []

    validate: () ->
        @errorMsg = ""
        @errorMsg += "The user name is required.  " if not @user.name

        return if @errorMsg.length > 0


    createUser: () ->
        if not @user.roles
            @user.roles = []

        @user.permissions = [{value: "test"}]
        @validate()
        return if @errorMsg.length > 0

        @SqlQueriesService.create('/users/create', @user)
        .then(
            (data) =>
                @$log.debug "Created #{data} User"
                @errorMsg = data
                @$rootScope.user = @user
                @$location.path("/user/edit")
            ,
            (error) =>
                @$log.error "Unable to create User: #{error}"
                @errorMsg = "Unable to create User: #{error}"
                return
            )

    updateUser: () ->
        if not @user.roles
            @user.roles = []
        @validate()
        return if @errorMsg.length > 0

        @SqlQueriesService.update("/users/update/#{@user.name}", @user)
        .then(
            (data) =>
                @$log.debug "Updated #{data} User"
                @errorMsg = data
#                @$location.path("/users")
            ,
            (error) =>
                @$log.error "Unable to update User: #{error}"
                @errorMsg = "Unable to update User: #{error}"
                return
            )

    cancel: () ->
        @$location.path('/users')

#    editRoles: ()


controllersModule.controller('UserCtrl', UserCtrl)

