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
        @$log.debug "deleteQuery() #{name}"
        BootstrapDialog.confirm(
            "Do you want to delete #{name} query"
            (result) =>
                if result
                    @SqlQueriesService.deleteByName("/sqlqueries/deletebyname/#{name}")
                    .then(
                        (data) =>
                            @$log.debug "Promise returned #{data.length} queries"
                            @getAllQueries()
                        ,
                        (error) =>
                            @$log.error "Unable to get Sql Queries: #{error}"
                        )
                else
                    @$log.debug "deleteQuery() #{name}"
                )


controllersModule.controller('SqlQueriesCrtl', SqlQueriesCrtl)