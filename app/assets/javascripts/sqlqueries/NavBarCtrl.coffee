class NavBarCrtl

	constructor: (@$location, @$log, @SqlQueriesService) ->
		@$log.debug "constructing Nav Bar Controller"


	goToLocation: (path) ->
        @$log.debug "NavBar.goToLocation()"
        @$location.path path


controllersModule.controller('NavBarCrtl', NavBarCrtl)