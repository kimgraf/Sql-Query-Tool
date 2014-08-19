"use strict"

angular.module('fileUpload', [ 'angularFileUpload' ]);

var NewDriverInstanceCtrl = [ '$scope', '$http', '$timeout', '$upload', 'SqlQueriesService', function($scope, $http, $timeout, $upload, SqlQueriesService) {
    var modalInstance;
	$scope.fileReaderSupported = window.FileReader != null;
	$scope.uploadRightAway = false;
	$scope.drivername;
	$scope.driverClass;
	$scope.driverFiles = []
//	$scope.selectedFiles = [];

	$scope.changeAngularVersion = function() {
		window.location.hash = $scope.angularVersion;
		window.location.reload(true);
	};
    $scope.ok = function(db) {
        $scope.errorMsg = '';
//        if(!db.drivername || db.drivername.length < 1) {
//            $scope.errorMsg = "The driver name is required."
//        }
//        if(!db.driverclass || db.driverclass.length < 1) {
//            if($scope.errorMsg)
//                $scope.errorMsg += "  "
//            $scope.errorMsg += "The driver class is required."
//        }
//        if(!$scope.selectedFiles || $scope.selectedFiles.length < 1) {
//            if($scope.errorMsg)
//                $scope.errorMsg += "  "
//            $scope.errorMsg += "The driver file is required."
//        }
//		if($scope.errorMsg){
//		    return;
//		}

//		if ($scope.driverfiles && $scope.driverfiles.length > 0) {
//            $scope.driverfiles.splice(0,$scope.driverfiles.length)
//		}

		for ( var i = 0; i < $scope.selectedFiles.length; i++) {
		    $scope.start(i);
//		    $scope.driverFiles[$scope.driverFiles.length] = $scope.selectedFiles[i].name;
		}

		if($scope.errorMsg){
		    return;
		}

//		var jdvr = {'name': $scope.drivername, 'driverClass':$scope.driverclass}
//		var jdvr = {'name': $scope.drivername, 'driverClass':$scope.driverclass, 'driverFiles':$scope.driverfiles}
//		var promise = SqlQueriesService.create(jdvr, '/drivers/create')
//		promise.then(
//            function success(data ){
//                console.log("success " + data)
//                NewDriverInstanceCtrl.modalInstance.close(jdvr)
//            }, function error(data){
//                console.log("error " + data)
//                $scope.errorMsg = data
//            }
//		)

    }

    $scope.cancel = function() {
        NewDriverInstanceCtrl.modalInstance.dismiss('cancel')
    }

	$scope.hasUploader = function(index) {
		return $scope.upload[index] != null;
	};
	$scope.abort = function(index) {
		$scope.upload[index].abort();
		$scope.upload[index] = null;
	};
	$scope.angularVersion = window.location.hash.length > 1 ? window.location.hash.substring(1) : '1.2.11';
	$scope.onFileSelect = function($files) {
		$scope.selectedFiles = [];
		$scope.progress = [];
		if ($scope.upload && $scope.upload.length > 0) {
            $scope.upload.splice(0,upload.length)
		}
		if ($scope.selectedFiles && $scope.selectedFiles.length > 0) {
            $scope.selectedFiles.splice(0,$scope.selectedFiles.length)
		}
		$scope.upload = [];
		$scope.uploadResult = [];
//		$scope.selectedFiles = $files;
		$scope.dataUrls = [];
		for ( var i = 0; i < $files.length; i++) {
			var $file = $files[i];
			var isJar = $file.name.endsWith('.jar');
			if (window.FileReader && isJar) {
			    if($scope.selectedFiles.indexOf($file.name) < 0)
			        $scope.selectedFiles.push($file);

				var fileReader = new FileReader();
				fileReader.readAsDataURL($files[i]);
				var loadFile = function(fileReader, index) {
					fileReader.onload = function(e) {
						$timeout(function() {
							$scope.dataUrls[index] = e.target.result;
						});
					}
				}
				loadFile(fileReader, i);
			}
//			if (!$file.type.indexOf('application/x-java-archive') > -1) {
//			    delete $files[i]
//			}
			$scope.progress[i] = -1;
			if ($scope.uploadRightAway) {
				$scope.start(i);
			}
		}
	};

	$scope.start = function(index) {
		$scope.progress[index] = 0;
		$scope.errorMsg = null;
		if ($scope.howToSend == 1) {
			$scope.upload[index] = $upload.upload({
				url : 'upload',
				method: $scope.httpMethod,
				headers: {'my-header': 'my-header-value'},
				data : {
					myModel : $scope.myModel
				},
				/* formDataAppender: function(fd, key, val) {
					if (angular.isArray(val)) {
                        angular.forEach(val, function(v) {
                          fd.append(key, v);
                        });
                      } else {
                        fd.append(key, val);
                      }
				}, */
				/* transformRequest: [function(val, h) {
					console.log(val, h('my-header')); return val + '-modified';
				}], */
				file: $scope.selectedFiles[index],
				fileFormDataName: 'myFile'
			});
			$scope.upload[index].then(function(response) {
				$timeout(function() {
					$scope.uploadResult.push(response.data);
				});
			}, function(response) {
				if (response.status > 0) $scope.errorMsg = response.status + ': ' + response.data;
			}, function(evt) {
				// Math.min is to fix IE which reports 200% sometimes
				$scope.progress[index] = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
			});
			$scope.upload[index].xhr(function(xhr){
//				xhr.upload.addEventListener('abort', function() {console.log('abort complete')}, false);
			});
		} else {
			var fileReader = new FileReader();
			fileReader.onloadend = function(e) {
                console.log(e)
            }
            fileReader.onload = function(e) {
		        $scope.upload[index] = $upload.http({
		        	url: '/drivers/upload',
					headers: {'Content-Type': $scope.selectedFiles[index].type, 'File-Name': $scope.selectedFiles[index].name},
					data: e.target.result
		        }).then(function(response) {
					$scope.uploadResult.push(response.data);
				}, function(response) {
					if (response.status > 0) $scope.errorMsg = response.status + ': ' + response.data;
				}, function(evt) {
					// Math.min is to fix IE which reports 200% sometimes
					$scope.progress[index] = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
				});
            }
	        fileReader.readAsArrayBuffer($scope.selectedFiles[index]);
		}
	};

	$scope.dragOverClass = function($event) {
		var items = $event.dataTransfer.items;
		var hasFile = false;
		if (items != null) {
			for (var i = 0 ; i < items.length; i++) {
				if (items[i].kind == 'file') {
					hasFile = true;
					break;
				}
			}
		} else {
			hasFile = true;
		}
		return hasFile ? "dragover" : "dragover-err";
	};
} ];

controllersModule.controller('NewDriverInstanceCtrl', NewDriverInstanceCtrl)