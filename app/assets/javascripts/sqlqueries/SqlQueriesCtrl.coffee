class SqlQueriesCrtl

	constructor: (@$scope, @$location, @$rootScope, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Sql Queries Controller"
		@searchText
		@queries = []
		@$scope.queryData = {}
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
		        for query in @queries
		            @$scope.queryData[query.name] = {'gridOptions': {gridDim: null, data: null}}
		    ,
		    (error) =>
		        @$log.error "Unable to get Sql Queries: #{error}"
		    )

	createSqlQuery: () ->
        @$log.debug "createSqlQuery()"
        @$rootScope.sqlquery = null
        @$location.path "/sqlquery/new"

    runQuery: (index) ->
        @$log.debug "runQuery() #[name}"
        @SqlQueriesService.findByName("/sqlqueries/findbyname/#{@queryData[index].name}")
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} queries"
                @queryData[index] = null
                @SqlQueriesService.post("/sqlqueries/run", data)
                .then(
                    (res) =>
                        @$scope.myData = res.rows
                    ,
                    (error) =>
                        @errorMsg = JSON.stringify(error)
                        @$log.error "Unable to post test JDBC Database: #{error.error}"
                    )
            ,
            (error) =>
                @$log.error "Unable to get Drivers: #{error}"
            )


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