var app = angular.module('AgentModule',[]);

app.controller('agentController', ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http){
	
	$rootScope.action = {
			valueREST : false,
			valueSocket : false
	};
	
	$scope.agentCollections = {
			agentTypes : [],
			runningAgents : [],
			messages : []
	};
	
	$scope.Console = {
			buttonChat : "Show comm. chat",
			show : false
	}
	
	$scope.showChat = function(){
		if(!$scope.Console.show){
			$scope.Console.show = true;
			$scope.Console.buttonChat = "Disable comm. chat";
		}else{
			$scope.Console.show = false;
			$scope.Console.buttonChat = "Show comm. chat";
		}
	};
	
	$scope.getTypes = function(){
		if($rootScope.action.valueREST){
			$http.get('/TheAgency/rest/agency/agents/classes').then(function(response){
				response.data.forEach(function(el){
					if($scope.agentCollections.agentTypes.indexOf(el) == -1)
						$scope.agentCollections.agentTypes.push(el);
				});
			});
		}else if($rootScope.action.valueSocket){
			//ws message
		}
	};
	
	$scope.getRunning = function(){
		if($rootScope.action.valueREST){
			$http.get('/TheAgency/rest/agency/agents/running').then(function(response){
				response.data.forEach(function(el){
					if($scope.agentCollections.runningAgents.indexOf(el) == -1)
						$scope.agentCollections.runningAgents.push(el);
				});
			});
		}else if($rootScope.action.valueSocket){
			//ws message
		}
	};
	
}]);