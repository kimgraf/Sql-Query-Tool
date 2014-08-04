"use strick"
class DriverCtrl

    constructor: (@$log, @$rootScope, @$location, @$timeout, @$upload, @SqlQueriesService) ->
        @$log.debug "constructing DriverCtrl"
        @upload = []
        @uploadResult = []
        @dataUrls = []
        @selectedFiles = []
        @progress = []
        @errorMsg
        @driver = {}
        @isEdit = false
        if @$rootScope.driver
            @driver = @$rootScope.driver
            @isEdit = true
#            @selectedFiles = @driver.driverFiles


    hasUploader: (index) ->
        return @upload[index] != null;


    onFileSelect: (files) ->
        @selectedFiles = []
        @progress = []
        if @upload && @upload.length > 0
            @upload.splice(0,upload.length)

        if @selectedFiles and @selectedFiles.length > 0
            @selectedFiles.splice(0,@selectedFiles.length)

        @upload = []
        @uploadResult = []
        @dataUrls = []
        @driver.driverFiles = []
        for  file in files
            isJar = file.name.endsWith('.jar')
            if (window.FileReader && isJar)
                if(@selectedFiles.indexOf(file.name) < 0)
                    @selectedFiles.push(file)
                    @driver.driverFiles.push(file.name)

                fileReader = new FileReader()
                fileReader.readAsDataURL(file)
                loadFile = (fileReader, index) =>
                    fileReader.onload = (e) =>
                        @$timeout(() =>
                            @dataUrls[index] = e.target.result)
#                loadFile(fileReader, _i)

            if !file.type.indexOf('application/x-java-archive') > -1
                delete files[_i]

            @progress[_i] = -1;


    start: (index) ->
        @progress[index] = 0
        @errorMsg = ""
        fileReader = new FileReader()
        fileReader.onload = (e) =>
            @upload[index] = @$upload.http(
                url: '/drivers/upload',
                headers: {'Content-Type': @selectedFiles[index].type, 'File-Name': @selectedFiles[index].name},
                data: e.target.result
            )
            .then(
                (response) =>
                    @uploadResult.push(response.data)
                ,
                (response) =>
                    errorMsg = response.status + ' : ' + response.data if response.status > 0
                ,
                (evt) =>
                    @progress[index] = Math.min(100, parseInt(100.0 * evt.loaded / evt.total))
                )

        fileReader.readAsArrayBuffer(@selectedFiles[index])

    validate: () ->
        @errorMsg = ""
        @errorMsg += "The driver name is required.  " if not @driver.name
        @errorMsg += "The driver class is required.  " if not @driver.driverClass
        @errorMsg += "The driver file is required.  " if not @driver.driverFiles

        return if @errorMsg.length > 0

        @start(i) for file, i in @selectedFiles if @selectedFiles.length > 0


    createDriver: () ->
        @validate()
        return if @errorMsg.length > 0

        @SqlQueriesService.create('/drivers/create', @driver)
        .then(
            (data) =>
                @$log.debug "Created #{data} JDBC Driver"
                @database = data
                @$location.path("/drivers")
            ,
            (error) =>
                @$log.error "Unable to create JDBC Driver: #{error}"
                errorMsg = "Unable to create JDBC Driver: #{error}"
                return
            )

    updateDriver: () ->
        @validate()
        return if @errorMsg.length > 0

        @SqlQueriesService.update("/drivers/update/#{@driver.name}", @driver)
        .then(
            (data) =>
                @$log.debug "Updated #{data} JDBC Driver"
                @database = data
                @$location.path("/drivers")
            ,
            (error) =>
                @$log.error "Unable to update JDBC Driver: #{error}"
                errorMsg = "Unable to update JDBC Driver: #{error}"
                return
            )

    cancel: () ->
        @$location.path('/drivers')

controllersModule.controller('DriverCtrl', DriverCtrl)

