var app = angular.module('routes', ['ngRoute']);

app.config(['$routeProvider', function($routeProvider){
	
	$routeProvider
			.when('/',{
				templateUrl: 'resources/html/main_page.html'
			})
	
	
}]);