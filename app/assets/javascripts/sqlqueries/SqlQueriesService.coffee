
class SqlQueriesService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing SqlQueriesService"

    findByName: (url) ->
        @$log.debug "create #{url}"
        deferred = @$q.defer()

        @$http.get(url)
        .success((data, status, headers) =>
                @$log.info("Successfully created - status #{status}")
                @$log.info(data)
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    post: (url, jsondata) ->
        @$log.debug "post #{angular.toJson(jsondata, true)}"
        deferred = @$q.defer()

        @$http.post(url, jsondata)
        .success((data, status, headers) =>
                @$log.info("Successfully posted - status #{status}")
                @$log.info(data)
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to post - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    create: (url, jsondata) ->
        @$log.debug "create #{angular.toJson(jsondata, true)}"
        deferred = @$q.defer()

        @$http.post(url, jsondata)
        .success((data, status, headers) =>
                @$log.info("Successfully created - status #{status}")
                @$log.info(data)
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to create - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    update: (url, jsondata) ->
        @$log.debug "update #{angular.toJson(jsondata, true)}"
        deferred = @$q.defer()

        @$http.post(url, jsondata)
        .success((data, status, headers) =>
                @$log.info("Successfully updated - status #{status}")
                @$log.info(data)
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to update - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

    list: (requestString, collectionString) ->
        @$log.debug "list()"
        deferred = @$q.defer()

        @$http.get(requestString)
        .success((data, status, headers) =>
                @$log.info("Successfully listed #{collectionString} - status #{status}")
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list - status #{status}")
                deferred.reject(data);
            )
        deferred.promise

servicesModule.service('SqlQueriesService', SqlQueriesService)