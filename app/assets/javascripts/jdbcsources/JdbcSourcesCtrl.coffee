class JdbcSourcesCrtl

	constructor: (@$location, @$log, @JdbcSourcesService) ->
		@$log.debug "constructing JDBC Sources Controller"
		@sources = []
		@getAllSources()
	
	getAllSources: () ->
		@$log.debug "getAllSources()"
	
		@JdbcSourcesService.listJdbcSources()
		.then(
		    (data) =>
		        @$log.debug "Promise returned #{data.length} Sources"
		        @sources = data
		    ,
		    (error) =>
		        @$log.error "Unable to get JDBC Sources: #{error}"
		    )

	createJdbcSource: () ->
	    @$log.debug "createJdbcSource()"

	    @$location.path "/jdbctemplates/create"
	
	
controllersModule.controller('JdbcSourcesCtrl', JdbcSourcesCrtl)
