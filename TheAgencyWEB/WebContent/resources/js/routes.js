var app = angular.module('routes', ['ngRoute']);

app.config(['$routeProvider', function($routeProvider){
	
	$routeProvider
			.when('/', {
				templateUrl: 'resources/html/main_page.html'
			})
			.when('/agents', {
				templateUrl: 'resources/html/agents.html'
			})
			.when('/weather', {
				templateUrl: 'resources/html/weather.html'
			})
	
	
}]);