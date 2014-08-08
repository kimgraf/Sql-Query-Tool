class DatabasesCrtl

	constructor: (@$rootScope, @$location, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Databases Controller"
		@databases = []
		@getAllDatabases()

	editDatabase: (name) ->
        @$log.debug "editDatabase() #[name}"
        @SqlQueriesService.findByName("/databases/findbyname/#{name}")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} queries"
                @$rootScope.database = data
                @$location.path("/databases/edit")
            ,
            (error) =>
                @$log.error "Unable to get Drivers: #{error}"
            )


	getAllDatabases: () ->
        @$log.debug "getAllDatabases()"
        @SqlQueriesService.list("/databases", "JDBC Databases")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} queries"
                @databases = data
            ,
            (error) =>
                @$log.error "Unable to get Databases: #{error}"
            )

    newDatabase: () ->
        @$log.debug "newDatabase()"
        @$rootScope.database = null
        @$location.path("/databases/new")


controllersModule.controller('DatabasesCrtl', DatabasesCrtl)