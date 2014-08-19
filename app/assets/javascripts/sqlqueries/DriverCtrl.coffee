"use strick"
class DriverCtrl

    constructor: (@$log, @$rootScope, @$location, @$timeout, @$upload, @$http, @SqlQueriesService) ->
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
        @upload = []
        @uploadResult = []
        @dataUrls = []
        @driver.driverFiles = []
        if @upload && @upload.length > 0
            @upload.splice(0,upload.length)

        if @selectedFiles and @selectedFiles.length > 0
            @selectedFiles.splice(0,@selectedFiles.length)

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

                loadFile(fileReader, _i)

            if !file.type.indexOf('application/x-java-archive') > -1
                delete files[_i]

            @progress[_i] = -1;


    start: (index) ->
        @progress[index] = 0
        @errorMsg = ""
        @upload[index] = @$upload.upload(
            url: '/drivers/upload',
            method: 'POST',
            headers: {'Content-Type': @selectedFiles[index].type, 'File-Name': @selectedFiles[index].name},
            file: @selectedFiles[index],
            fileFormDataName: 'jarFile'
        )
        @upload[index].then(
            (response) =>
                @$timeout(() =>
                    @uploadResult.push(response.data))
            ,
            (response) =>
                errorMsg = response.status + ' : ' + response.data if response.status > 0
            ,
            (evt) =>
                @progress[index] = Math.min(100, parseInt(100.0 * evt.loaded / evt.total))
            )

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
                @errorMsg = data
                @$rootScope.driver = @driver
                @$location.path("/drivers/edit")
            ,
            (error) =>
                @$log.error "Unable to create JDBC Driver: #{error}"
                @errorMsg = "Unable to create JDBC Driver: #{error}"
                return
            )

    updateDriver: () ->
        @validate()
        return if @errorMsg.length > 0

        @SqlQueriesService.update("/drivers/update/#{@driver.name}", @driver)
        .then(
            (data) =>
                @$log.debug "Updated #{data} JDBC Driver"
                @errorMsg = data
#                @$location.path("/drivers")
            ,
            (error) =>
                @$log.error "Unable to update JDBC Driver: #{error}"
                @errorMsg = "Unable to update JDBC Driver: #{error}"
                return
            )

    cancel: () ->
        @$location.path('/drivers')

controllersModule.controller('DriverCtrl', DriverCtrl)

