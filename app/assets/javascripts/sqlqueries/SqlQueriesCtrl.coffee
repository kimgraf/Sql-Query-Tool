class SqlQueriesCrtl

	constructor: (@$location, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Sql Queries Controller"
		@queries = []
		@getAllQueries()


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

	    @$location.path "/sqlquery/new"


controllersModule.controller('SqlQueriesCrtl', SqlQueriesCrtl)