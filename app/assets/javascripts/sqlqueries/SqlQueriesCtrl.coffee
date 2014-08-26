class SqlQueriesCrtl

	constructor: (@$location, @$rootScope, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Sql Queries Controller"
		@queries = []
		@getAllQueries()

	editQuery: (name) ->
        @$log.debug "editQuery() #[name}"
        @SqlQueriesService.findByName("/sqlqueries/findbyname/#{name}")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} queries"
                @$rootScope.sqlquery = data
                @$location.path("/sqlquery/edit")
            ,
            (error) =>
                @$log.error "Unable to get Drivers: #{error}"
            )

	getAllQueries: () ->
		@$log.debug "getAllQueries()"

		@SqlQueriesService.list("/sqlqueries", "SQL Queries")
		.then(
		    (data) =>
		        @$log.debug "Promise returned #{data.length} queries"
		        @queries = data
		    ,
		    (error) =>
		        @$log.error "Unable to get Sql Queries: #{error}"
		    )

	createSqlQuery: () ->
        @$log.debug "createSqlQuery()"
        @$rootScope.sqlquery = null
        @$location.path "/sqlquery/new"

	deleteQuery: (name) ->
	    @getAllQueries()
	    @log.debug "deleteQuery() #{name}"



controllersModule.controller('SqlQueriesCrtl', SqlQueriesCrtl)