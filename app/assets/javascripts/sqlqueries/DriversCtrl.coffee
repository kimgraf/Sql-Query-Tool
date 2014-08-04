class DriversCrtl

	constructor: (@$rootScope, @$location, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Drivers Controller"
		@drivers = []
		@getAllDrivers()


	getAllDrivers: () ->
        @$log.debug "getAllDrivers()"
        @SqlQueriesService.list("/drivers", "JDBC Drivers")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} queries"
                @drivers = data
            ,
            (error) =>
                @$log.error "Unable to get Drivers: #{error}"
            )

    newDriver: () ->
        @$log.debug "newDrivers()"
        @$rootScope.driver = null
        @$location.path("/drivers/new")

    editDriver: (name) ->
        @$log.debug "editDriver() #{name}"
        @SqlQueriesService.findByName("/drivers/findbyname/#{name}")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} queries"
                @$rootScope.driver = data
                @$location.path("/drivers/edit")
            ,
            (error) =>
                @$log.error "Unable to get Drivers: #{error}"
            )




controllersModule.controller('DriversCrtl', DriversCrtl)