var app = angular.module('AgentFactory', []);

app.factory('RestFactory', '$http', function($http){
	
	factory = {};
	
	factory.getAgentTypes = function(){
		return $http.get('/TheAgency/rest/agency/agents/classes');
	}
	
	return factory;
});