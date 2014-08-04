
class SqlQueryCtrl

    constructor: (@$log, @$location,  @SqlQueriesService) ->
        @$log.debug "constructing SqlQueryCtrl"
        @sqlquery
        @createurl = "/sqlqueries/new"
        @databases
        @database
        @name
        @queryText
        @errorMsg
        @loadDatabases()

    createSqlQuery: () ->
        @$log.debug "createSqlQuery()"
        @errorMsg =""
        @errorMsg += "Name is required " if not @name
        @errorMsg += "Query is required " if not @queryText
        @errorMsg += "Database is required " if not @database

        if @errorMsg.length > 0
            @$log.error @errorMsg
            return

        @sqlquery = {name: @name, queryText:@queryText, database: @database.name }

        @SqlQueriesService.create(@createurl, @sqlquery)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} JDBC Source"
                @sqlquery = data
                @$location.path("/sqlquery")
            ,
            (error) =>
                @$log.error "Unable to create Sql Query: #{error}"
            )

    newDatabase: () ->
        @$log.debug "newDatabase()"
        @$location.path("/databases/new")

    loadDatabases: () ->
        @SqlQueriesService.list("/databases/list", "JDBC Databases")
            .then(
                (data) =>
                    @$log.debug "Promise returned #{data} JDBC Databases"
                    @databases = data
                    if @database
                        for d in @databases
                            if d.name is @database.name
                                @database = d
                ,
                (error) =>
                    @$log.error "Unable to load Jdbc Databases: #{error}"
                )

controllersModule.controller('SqlQueryCtrl', SqlQueryCtrl)