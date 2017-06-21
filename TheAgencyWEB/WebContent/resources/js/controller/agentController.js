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
	
	$scope.agentInfo = {
			name : "",
			run : false,
	}
	
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
	
	$scope.showRunOptions = function(){
		if($scope.agentInfo.run)
			$scope.agentInfo.run = false;
		else
			$scope.agentInfo.run = true;
	}
	
	var checkRunningAgents = function(array, el){
		for(x in array){
			if(array[x].name === el.name &&
			   array[x].host.alias === el.host.alias &&
			   array[x].type.module === el.type.module &&
			   array[x].type.name === el.type.name)
			return true;
		}
		return false;
	}
		
	//Get all agent types
	$scope.getTypes = function(){
		if($rootScope.action.valueREST){
			$http.get('/TheAgency/rest/agency/agents/classes').then(function(response){
				response.data.forEach(function(el){
					for(x in $scope.agentCollections.agentTypes){
						if($scope.agentCollections.agentTypes[x].name === el.name && 
						   $scope.agentCollections.agentTypes[x].module === el.module)
							return;
					}
						$scope.agentCollections.agentTypes.push(el);
				});
			});
		}else if($rootScope.action.valueSocket){
			//ws message
		}
	};
	
	//Get all running agents
	$scope.getRunning = function(){
		if($rootScope.action.valueREST){
			$http.get('/TheAgency/rest/agency/agents/running').then(function(response){
				response.data.forEach(function(el){
					if(checkRunningAgents($scope.agentCollections.runningAgents, el))
						return;
					$scope.agentCollections.runningAgents.push(el);
				});
			});
		}else if($rootScope.action.valueSocket){
			//ws message
		}
	};
	
	//startAgent
	$scope.startAgent = function(typeModule, typeName){
		if($rootScope.action.valueREST){
			$http.put('/TheAgency/rest/agency/agents/running/'+typeModule+":"+typeName+"/"+$scope.agentInfo.name)
				.then(function(response){
					var aid = response.data;
					if(aid === undefined || aid === null || aid === ""){
						alert("Agent can't be started. Possibly the same name or some other error.");
						return;
					}
					if(checkRunningAgents($scope.agentCollections.runningAgents, aid))
						return;
					$scope.agentCollections.runningAgents.push(aid);
					$scope.agentInfo.name = "";
				 });
			}else if($rootScope.action.valueSocket){
				//ws
			}
		};
		
	$scope.stopAgent = function(AID){
		if($rootScope.action.valueREST){
			var data = angular.toJson(AID);
			$http({
				method: 'DELETE',
				url: '/TheAgency/rest/agency/agents/running',
				data: data,
				headers: {
					'Content-type': 'application/json;charset=utf-8'
				}
			})
				 .then(function(response){
					 var res = response.data;
					 if(res === undefined || res === null || res === ""){
						 alert("Agent can't be stopped.");
						 return;
					 }
					 var index = -1;
					 for(x = 0; x < $scope.agentCollections.runningAgents.length; x++){
						 if($scope.agentCollections.runningAgents[x].name === res.name)
							 index = x;
					 }
					 if(index > -1)
						 $scope.agentCollections.runningAgents.splice(index, 1);
				 });
		}else if($rootScope.action.valueSocket){
			//ws
		}
	};
	
}]);