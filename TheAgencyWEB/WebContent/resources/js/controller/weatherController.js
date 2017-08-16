var app = angular.module('weather', []);

app.controller('weatherController', ['$scope', '$http', '$rootScope', function($scope, $http, $rootScope){
		
	var url = window.location;
	
	var wsaddress = "ws://"+url.hostname+":"+url.port+"/TheAgency/agents";
	
	$rootScope.action = {
			valueREST: false,
			valueSocket: false
	}
	
	$scope.weatherMaster = {
			name : "WeatherMaster",
			module : "ejbModule.agents",
			agentName: ""
	}

	$scope.collections = {
			runningAgents : []
	}
	
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
			inReplyTo : "",
			accu: false,
			umbrella: false,
			mix: false
		};
	
	$scope.showAgents = true;
	
	$scope.text = "Hide running weather agents"
	
	$scope.setRest = function() {
		$rootScope.action.valueREST = true;
		$rootScope.action.valueSocket = false;
	}
	
	$scope.setSocket = function() {
		$rootScope.action.valueREST = false;
		$rootScope.action.valueSocket = true;
	}
	
	$scope.hideRunning = function() {
		if($scope.showAgents) {
			$scope.showAgents = false;
			$scope.text = "Show running weather agents"
		} else {
			$scope.showAgents = true;
			$scope.text = "Hide running weather agents"
		}
	}
	
	$scope.setAccu = function() {
		$scope.ACLMessage.accu = true;
		$scope.ACLMessage.umbrella = false;
		$scope.ACLMessage.mix = false;
	}
	
	$scope.setUmbrella = function() {
		$scope.ACLMessage.accu = false;
		$scope.ACLMessage.umbrella = true;
		$scope.ACLMessage.mix = false;
	}
	
	$scope.setMix = function() {
		$scope.ACLMessage.accu = false;
		$scope.ACLMessage.umbrella = false;
		$scope.ACLMessage.mix = true;
	}
	try{
		var socket = new WebSocket(wsaddress);
		
		socket.onopen = function() {
			console.log("Socket for weather open")
		}
		
		socket.onclose = function() {
			socket.close();
			console.log("Socket closed")
		}
		
		socket.onmessage = function(message) {
			var socketMessage = JSON.parse(message.data);
			switch(socketMessage.msgType){
			case 'START_AGENT': socketStartAgents(socketMessage); break;
			case 'STOP_AGENT': socketStopAgents(socketMessage); break;
			}
		}
	} catch(exception) {
		
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
	
	var socketStartAgents = function(socketMessage){
		if(checkRunningAgents($scope.collections.runningAgents, socketMessage.aid))
			return;
		$scope.$apply(function(){
			$scope.collections.runningAgents.push(socketMessage.aid);
			$scope.weatherMaster.agentName = "";
		});
	}
	
	
	var socketStopAgents = function(socketMessage){
		 var index = -1;
		 for(x = 0; x < $scope.collections.runningAgents.length; x++){
			 if($scope.collections.runningAgents[x].name === socketMessage.aid.name){
				 index = x;
				 break;
			 }
		 }
		 if(index > -1)
			 $scope.$apply(function(){
				 $scope.collections.runningAgents.splice(index, 1);
			 });
	}

	$scope.startAgent = function(){
		if($rootScope.action.valueREST){
			$http.put('/TheAgency/rest/agency/agents/running/'+$scope.weatherMaster.module+":"+$scope.weatherMaster.name+"/"+$scope.weatherMaster.agentName)
				.then(function(response){
					var aid = response.data;
					if(aid === undefined || aid === null || aid === ""){
						alert("Agent can't be started. Possibly the same name or some other error.");
						return;
					}
					if(checkRunningAgents($scope.collections.runningAgents, aid))
						return;
					$scope.collections.runningAgents.push(aid);
					$scope.agentName = "";
				 });
			}else if($rootScope.action.valueSocket){
				var socketMessage = {
						msgType : 'START_AGENT',
						agentName : $scope.weatherMaster.agentName,
						typeName : $scope.weatherMaster.name,
						typeModule : $scope.weatherMaster.module
				};
				socket.send(JSON.stringify(socketMessage));
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
						 for(x = 0; x < $scope.collections.runningAgents.length; x++){
							 if($scope.collections.runningAgents[x].name === res.name) {
								 index = x;
								 break;
							 }
						 }
						 if(index > -1)
							 $scope.collections.runningAgents.splice(index, 1);
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
	
