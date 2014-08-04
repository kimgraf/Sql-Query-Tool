
class JdbcSourcesService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing JdbcSourcesService"

    listJdbcSources: () ->
        @$log.debug "listJdbcSourcess()"
        deferred = @$q.defer()

        @$http.get("/jdbcsources")
        .success((data, status, headers) =>
                @$log.info("Successfully listed JdbcSources - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list JdbcSources - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    createJdbcSource: (JdbcSource) ->
        @$log.debug "createJdbcSource #{angular.toJson(JdbcSource, true)}"
        deferred = @$q.defer()
        
        @$http.post('/jdbcsource', JdbcSource)
        .success((data, status, headers) =>
                @$log.info("Successfully created JdbcSource - status #{status}")
                @$log.info(data)
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create JdbcSource - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

servicesModule.service('JdbcSourcesService', JdbcSourcesService)