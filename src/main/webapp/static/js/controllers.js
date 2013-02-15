'use strict';

/* Controllers */

function InfoCtrl($scope, $http) {
	
	$scope.reload = function() {
		//$http.get('../rest/track/info/daniel').success(function(data) {
		$http.get('../rest/track/infos').success(function(data) {
	        $scope.infos = data;
	    });
	}
    
    $scope.timeleft = function(info) {
    	if (info) {
    		
    	    var negative = info.creditBalanceInSeconds < 0;
    	    var bal = Math.abs(info.creditBalanceInSeconds);
    		
	    	var hours = Math.floor(bal / 60 / 60);
	    	var minutes = Math.floor((bal - hours * 60 * 60) / 60);
	    	var seconds = bal - hours * 60 * 60 - minutes * 60;
	    	
	    	if (hours < 10) {
	    		hours = "0" + hours;
	    	}
	    	if (minutes < 10) {
	    		minutes = "0" + minutes;
	    	}
	    	if (seconds < 10) {
	    		seconds = "0" + seconds;
	    	}
	    	return (negative ? "- " : "") + hours+ ":" + minutes + ":" + seconds;
    	}
    	return "calculating..."
    }
    
    var isNumeric = function(val) {
    	return !isNaN(parseFloat(val)) && isFinite(val);
    }
    
    $scope.extraCreditData = 
    
    $scope.extraCredit = function(name) {
    	if ($scope.extraCreditData.credit && isNumeric($scope.extraCreditData.credit)) {
    		
    		var data = {
    	        	'username': name,
    	        	'minutes': $scope.extraCreditData.credit 
    	        };
    		//console.log('http POST with data: ' + data)
    		// jquery because of http://victorblog.com/2012/12/20/make-angularjs-http-service-behave-like-jquery-ajax/
    		jQuery.post('../rest/track/extracredit', data);

    	}
    }
    $scope.update = function(name) {
    	jQuery.post('../rest/track/update', {'username':name});
    	$scope.reload();
    }
    
    $scope.styleRemainingTime = function(number) {
    	return number > 0 ? "success" : "warning";
    	
    }
    
    $scope.reload();
}

