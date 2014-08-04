"use strick"
class DatabaseCtrl

    constructor: (@$log, @$rootScope, @$location, @SqlQueriesService) ->
        @$log.debug "constructing DatabaseCtrl"
        @drivers
        @driver
        @errorMsg = ""
        @driver = {}
        @database = {}
        @isEdit = false
        if @$rootScope.database
            @database = @$rootScope.database
            @isEdit = true
        @loadDrivers()

    validate: () ->
        @$log.debug "validate()"
        @errorMsg =""
        @errorMsg += "Name is required " if not @database.name
        @errorMsg += "Url is required " if not @database.url
        @errorMsg += "Driver is required " if not @driver

    createDatabase: () ->
        @validate()
        if @errorMsg.length > 0
            @$log.error @errorMsg
            return

        @database =
            'name': @database.name
            'url':@database.url
            'userName': @database.userName
            'password': @database.password
            'driver': @driver.name

        @SqlQueriesService.create("/databases/create", @database)
        .then(
            (data) =>
                @$log.debug "Created #{data} JDBC Database"
                @database = data
                @$location.path("/databases")
            ,
            (error) =>
                @$log.error "Unable to create JDBC Database: #{error}"
            )

    updateDatabase: () ->
        @validate()
        if @errorMsg.length > 0
            @$log.error @errorMsg
            return

        @database =
            'name': @database.name
            'url':@database.url
            'userName': @database.userName
            'password': @database.password
            'driver': @driver.name

        @SqlQueriesService.create("/databases/update/#{@database.name}", @database)
        .then(
            (data) =>
                @$log.debug "Updated #{data} JDBC Database"
                @database = data
                @$location.path("/databases")
            ,
            (error) =>
                @$log.error "Unable to update JDBC Database: #{error}"
            )

    newDriver: () ->
        @$log.debug "newDriver()"
        @$rootScope.database = null
        @database = {}
        @driver = {}
        @$location.path("/drivers/new")


    loadDrivers: () ->
        @SqlQueriesService.list("/drivers", "JDBC Drivers")
            .then(
                (data) =>
                    @$log.debug "Promise returned #{data} JDBC Drivers"
                    @drivers = data
                    for d in @drivers
                        if @database and d.name is @database.driver
                            @driver = d
                ,
                (error) =>
                    @$log.error "Unable to load Jdbc Drivers: #{error}"
                )

    testConnection: () ->
        @$log.debug "DatabaseCtrl.testConnection()"
        @validate()
        if @errorMsg.length > 0
            @$log.error @errorMsg
            return

        @database =
            'name': @database.name
            'url':@database.url
            'userName': @database.userName
            'password': @database.password
            'driver': @driver.name

        @SqlQueriesService.post("/databases/testconnection", @database)
        .then(
            (data) =>
                @$log.debug "Post test #{data} JDBC Database"
                @errorMsg = JSON.stringify(data)
#                @$location.path("/databases")
            ,
            (error) =>
                @$log.error "Unable to post test JDBC Database: #{error}"
                @errorMsg = error
            )


controllersModule.controller('DatabaseCtrl', DatabaseCtrl)
