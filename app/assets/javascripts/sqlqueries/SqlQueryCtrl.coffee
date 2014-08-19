
class SqlQueryCtrl

    constructor: (@$scope, @$log, @$rootScope, @$location,  @SqlQueriesService) ->
        @$log.debug "constructing SqlQueryCtrl"
        @databases
        @database = {}
        @name
        @queryText
        @errorMsg
        @sqlquery = {}
#        @gridOptions = {}
        $scope.myData = null

        @$scope.gridOptions = {data: 'myData'}
        @isEdit = false
        if @$rootScope.sqlquery
            @sqlquery = @$rootScope.sqlquery
            @isEdit = true
        @loadDatabases()

    validate: () ->
        @$log.debug "validate()"
        @sqlquery.database = @database.name
        @errorMsg =""
        @errorMsg += "Name is required " if not @sqlquery.name
        @errorMsg += "Query is required " if not @sqlquery.queryText
        @errorMsg += "Database is required " if not @sqlquery.database

    createSqlQuery: () ->

        @validate()

        if @errorMsg.length > 0
            @$log.error @errorMsg
            return


        @SqlQueriesService.create("/sqlqueries/create", @sqlquery)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} JDBC Source"
                @sqlquery = data
                @$location.path("/sqlquery")
            ,
            (error) =>
                @$log.error "Unable to create Sql Query: #{error}"
            )

    updateSqlQuery: () ->

        @validate()

        if @errorMsg.length > 0
            @$log.error @errorMsg
            return

        @SqlQueriesService.create("/sqlqueries/update/#{@sqlquery.name}", @sqlquery)
        .then(
            (data) =>
                @$log.debug "Updated #{data} Sql Query"
                @database = data
                @$location.path("/sqlqueries")
            ,
            (error) =>
                @$log.error "Unable to update Sql Query: #{error}"
            )

    newDatabase: () ->
        @$log.debug "newDatabase()"
        @$rootScope.database = null
        @database = {}
        @$location.path("/databases/new")

    loadDatabases: () ->
        @SqlQueriesService.list("/databases", "JDBC Databases")
            .then(
                (data) =>
                    @$log.debug "Promise returned #{data} JDBC Databases"
                    @databases = data
                    for d in @databases
                        if @sqlquery and d.name is @sqlquery.database
                            @database = d
                ,
                (error) =>
                    @$log.error "Unable to load Jdbc Databases: #{error}"
                )

    runQuery: () ->
        @$log.debug "runQuery() #{@sqlquery}"
        @validate()
        @$scope.gridOptions = null
        if @errorMsg.length > 0
            @$log.error @errorMsg
            return

        @SqlQueriesService.post("/sqlqueries/run", @sqlquery)
        .then(
            (res) =>
                @$scope.myData = res.rows
                @$scope.gridOptions = {data: 'myData'}
            ,
            (error) =>
                @errorMsg = JSON.stringify(error)
                @$log.error "Unable to post test JDBC Database: #{error.error}"
            )


controllersModule.controller('SqlQueryCtrl', SqlQueryCtrl)