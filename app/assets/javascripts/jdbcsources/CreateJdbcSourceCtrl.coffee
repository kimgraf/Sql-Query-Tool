
class CreateJdbcSourceCtrl

    constructor: (@$log, @$location,  @JdbcSourcesService) ->
        @$log.debug "constructing CreateJdbcSourceCtrl"
        @jdbcsource = {}
        @doSomeMethod()

    createJdbcSource: () ->
        
        @$log.debug "createJdbcSource()"
        @JdbcSourcesService.createJdbcSource(@jdbcsource)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data} JDBC Source"
                @jdbcsource = data
                @$location.path("/jdbcsource")
            ,
            (error) =>
                @$log.error "Unable to create JDBC Source: #{error}"
            )

    doSomeMethod: () ->
        @$log.debug "doSomeMethod"

controllersModule.controller('CreateJdbcSourceCtrl', CreateJdbcSourceCtrl)