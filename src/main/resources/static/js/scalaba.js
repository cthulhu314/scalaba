//main namespace
Scalaba = {}

Scalaba.Persistance = {}

Scalaba.Controllers = {}

Scalaba.Controllers.ThreadController = function($scope,$location,$routeParams,repository) {
    $scope.model = {}
    repository.get($routeParams.threadId).then(function(result) {
        $scope.model = result;
    },function(why) {
        $location.path('/not/found');
    });
};

Scalaba.Controllers.ThreadListController = function($scope,$location,repository) {
    $scope.model = {}
    $scope.model.threads = [];
    $scope.busy = false;
    $scope.loadThreads = function() {
        $scope.busy = true;
        repository.get({from: $scope.model.threads.length, count:10 }).then(function(result) {

            for (var i = result.length - 1; i >= 0; i--) {
                $scope.model.threads.push(result[i]);
            };
            $scope.busy = false;
        },function(why) {
            $location.path('/not/found');
            $scope.busy = false;
        });
    };
    repository.get().then(function(result) {
        $scope.model.threads = result;
    },function(why) {
        $location.path('/not/found');
    });
};

Scalaba.Controllers.NavigationController = function($scope,$location,$routeParams,repository) {
    $scope.threadFormOpened = false;
    $scope.model = {};
    $scope.alerts = [];
    $scope.warnings = [];
    $scope.successes = [];
    $scope.type = $routeParams.threadId == undefined?"Тред":"Пост";
    $scope.remove = function(collection,element) {
        var index = collection.indexOf(element);
        if (index > -1) {
            collection.splice(index, 1);
        }
    };
    $scope.triggerThreadCreationForm = function() {
        $scope.threadFormOpened = !$scope.threadFormOpened;
    };
    $scope.createThread = function() {
        var promise = $routeParams.threadId == undefined?repository.add($scope.model):repository.add($scope.model,$routeParams.threadId);
        promise.then($scope.successes.push, $scope.alerts.push);
        $scope.triggerThreadCreationForm();
    };

};

Scalaba.Persistance.RemoteRepository = function($http) {
    var collection = []
    this.get = function(id) {
        if(id == undefined) {
            return $http.get("/api/threads");
        } else if (id.from!=undefined && id.count!=undefined) {
            return $http.get("/api/threads",id)
        } else {
            return $http.get("/api/thread/"+id.toString())
        }
    };
    this.add = function(post,threadId) {
        if(threadId == undefined) {
            return $http.post("/api/thread",post);
        } else {
            return $http.post("/api/post",post);
        }
    }
}

Scalaba.Persistance.LocalRepository = function($q) {
    var collection = [{id: 0, op : {theme:"lol",author:"nyan",text:"oaisdhiHASDGAISDBQWKBDQJLHWE"},
                posts: [{theme:"lol1",author:"KJASJHDSA",text:"alkhdKLJSAKJHASDBJASD"}]}];

    this.get = function(id) {
        var promise = $q.defer();
        if(id == undefined) {
            promise.resolve(collection);
            
        } else if (id.from!=undefined && id.count!=undefined) {
            promise.resolve(collection.splice(id.from,id.count));
        } else {
            if(collection[id] == undefined) {
                promise.reject("Not found")   
            } else {
                promise.resolve(collection[id]);
            } 
            
        }
        return promise.promise;
    };
    this.add = function(post) {
        var promise = $q.defer();
        if(threadId == undefined) {
            collection.push({id: collection.length, op: post, posts: [] })
            promise.resolve("Добавлен")
        } else {
            var thread = collection[post.threadId];
            if(thread == undefined) {
                promise.reject("Тред не найден");  
            } else {
                thread.posts.push(post);
                promise.resolve('Добавлен');
            }
            
        }
        return promise.promise;
    };
}

$(document).ready(function() {
    moment();
    var app = angular.module('scalaba',['infinite-scroll']);
    app.config(['$routeProvider',function($routeProvider) {
        $routeProvider
            .when('',{
                redirectTo: '/threads'
            })
            .when('/threads',{
                templateUrl: "partials/thread-list.html",
                controller: "ThreadListController"
            })
            .when('/thread/:threadId', {
                templateUrl: "partials/thread-details.html",
                controller: "ThreadController"
            })
            .when('/not/found', {
                templateUrl: "partials/not-found.html"  
            })
            .otherwise({
                redirectTo: '/not/found' 
            });
    }]);
    app.factory('repository',function($http) {
        return new Scalaba.Persistance.RemoteRepository($http);
    });
    app.controller('ThreadController',['$scope','$location','$routeParams','repository',Scalaba.Controllers.ThreadController]);
    app.controller('ThreadListController',['$scope','$location','repository',Scalaba.Controllers.ThreadListController])
    app.controller('NavigationController',['$scope','$location','$routeParams','repository',Scalaba.Controllers.NavigationController])
});