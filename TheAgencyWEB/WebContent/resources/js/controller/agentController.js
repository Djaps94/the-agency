var app = angular.module('AgentModule',[]);

app.controller('agentController', ['$scope', '$rootScope', '$http', function($scope, $rootScope, $http){
	
	$rootScope.action = {
			valueREST : false,
			valueSocket : false
	};
	
	$scope.modalRunningAgents = [];
	$scope.modalPerformative = [];
	
	$scope.ACLMessage = {
		performative : "",
		sender : null,
		recievers : [],
		replyTo : null,
		content : "",
		language : "",
		encoding : "",
		protocol : "",
		ontology : "",
		replyWith : "",
		inReplyTo : ""
	};
	
	$scope.fillModal = function(){
		$http.get('/TheAgency/rest/agency/agents/running').then(function(response){
			response.data.forEach(function(el){
				if(!checkRunningAgents($scope.modalRunningAgents, el))
					$scope.modalRunningAgents.push(el);
			});
		});
		$http.get('/TheAgency/rest/agency/messages').then(function(response){
			$scope.modalPerformative = response.data;
		});
		
	};
	
	//Opening socket
	var url = window.location;
	var wsadress = "ws://"+url.hostname+":"+url.port+"/TheAgency/agents";
	
	
	try{
		socket = new WebSocket(wsadress);
		
		socket.onopen = function(){
			console.log("Socket towards agents opened.");
		}
		
		socket.onclose = function(){
			socket.close();
			console.log("Socket closed.");
		}
		
		socket.onmessage = function(message){
			var socketMessage = JSON.parse(message.data);
			if($rootScope.action.valueSocket){
				switch(socketMessage.msgType){
				case 'ADD_TYPE'   : socketAgentTypes(socketMessage);	break;
				case 'GET_TYPES'  : socketAgentTypes(socketMessage);	break;
				case 'GET_AGENTS' : socketRunningAgents(socketMessage); break;
				case 'START_AGENT': socketStartAgents(socketMessage);	break;
				case 'STOP_AGENT' : socketStopAgents(socketMessage);	break;
				}
			}
		}
		
	}catch(exception){
		console.log("exception");
	}
	
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
							continue;
					}
						$scope.agentCollections.agentTypes.push(el);
				});
			});
		}else if($rootScope.action.valueSocket){
			var socketMessage = {
					msgType : 'GET_TYPES'
			};
			socket.send(JSON.stringify(socketMessage));
		}
	};
	
	//get all agent types via socket
	var socketAgentTypes = function(socketMessage){
		socketMessage.agentTypes.forEach(function(el){
			for(x in $scope.agentCollections.agentTypes){
				if($scope.agentCollections.agentTypes[x].name === el.name && 
				   $scope.agentCollections.agentTypes[x].module === el.module)
					continue;
			}
				$rootScope.$apply(function(){
					$scope.agentCollections.agentTypes.push(el);
				});
		});
	}
	
	//Get all running agents
	$scope.getRunning = function(){
		if($rootScope.action.valueREST){
			$http.get('/TheAgency/rest/agency/agents/running').then(function(response){
				response.data.forEach(function(el){
					if(!checkRunningAgents($scope.agentCollections.runningAgents, el))
						$scope.agentCollections.runningAgents.push(el);
				});
			});
		}else if($rootScope.action.valueSocket){
			var socketMessage = {
					msgType : 'GET_AGENTS'
			};
			socket.send(JSON.stringify(socketMessage));
		}
	};
	
	//get all running agents via socket
	var socketRunningAgents = function(socketMessage){
		socketMessage.runningAgents.forEach(function(el){
			if(!checkRunningAgents($scope.agentCollections.runningAgents, el))
				$scope.$apply(function(){
					$scope.agentCollections.runningAgents.push(el);
				});
		});	
	}
	
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
				var socketMessage = {
						msgType : 'START_AGENT',
						agentName : $scope.agentInfo.name,
						typeName : typeName,
						typeModule : typeModule
				};
				socket.send(JSON.stringify(socketMessage));
			}
		};
	
	//start agents via socket
	var socketStartAgents = function(socketMessage){
		if(checkRunningAgents($scope.agentCollections.runningAgents, socketMessage.aid))
			return;
		$scope.$apply(function(){
			$scope.agentCollections.runningAgents.push(socketMessage.aid);
			$scope.agentInfo.name = "";
		});
	}
		
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
			var Aid = JSON.parse(angular.toJson(AID));
			var socketMessage = {
				msgType : 'STOP_AGENT',
				aid : Aid	
			};
			socket.send(JSON.stringify(socketMessage));
		}
	};
	
	var socketStopAgents = function(socketMessage){
		 var index = -1;
		 for(x = 0; x < $scope.agentCollections.runningAgents.length; x++){
			 if($scope.agentCollections.runningAgents[x].name === socketMessage.aid.name)
				 index = x;
		 }
		 if(index > -1)
			 $scope.$apply(function(){
				 $scope.agentCollections.runningAgents.splice(index, 1);
			 });
	}
	
	$scope.sendMessage = function(){
		var data = angular.toJson($scope.ACLMessage);
		if($rootScope.action.valueREST){
			$http({
				method : 'POST',
				url: '/TheAgency/rest/agency/messages',
				data: data,
				headers: {
					'Content-type' : 'application/json;charset=utf-8'
				}
			});
			socket.send(JSON.stringify(socketMessage));
			$scope.ACLMessage.content = "";
			$scope.ACLMessage.sender = null;
			$scope.ACLMessage.recievers = [];
			$scope.ACLMessage.replyTo = null;
			$scope.ACLMessage.performative = "";
		}else if($rootScope.action.valueSocket){
			var acl = JSON.parse(data);
			var socketMessage = {
				msgType : 'SEND_MESSAGE',
				message : acl
			};
			
			socket.send(JSON.stringify(socketMessage));
			$scope.ACLMessage.content = "";
			$scope.ACLMessage.sender = null;
			$scope.ACLMessage.recievers = [];
			$scope.ACLMessage.replyTo = null;
			$scope.ACLMessage.performative = "";
		}
	}
	
}]);