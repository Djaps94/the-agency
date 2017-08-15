var app = angular.module('weather', []);

app.controller('weatherController', ['$scope', '$http', function($scope, $http){
	
	$scope.weatherMaster = {
			name : "Weather Boy",
			module : "ejbModule.agents"
	}

	$scope.collections = {
			runningAgents : []
	}
	
	$scope.showAgents = true;

	$scope.startAgent = function() {
		
	}
}]);
	
