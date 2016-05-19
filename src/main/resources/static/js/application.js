'use strict'
var woodBarnApp = angular.module('woodBarnApp', []);

woodBarnApp.controller('WoodBarnController', function ($scope,$timeout,$http) {
    $scope.rowCount = 5;
    $scope.rows = [];
    $scope.focusableInputs = [];
    $scope.characters = [{char:'-'}];
    $scope.lengthCounter = [];
    $scope.lengthNeeded = 5;

    $scope.init = function(){
    for( var i = 0; i < $scope.rowCount; i++ ){
        var row = [];
        for( var j=0; j<$scope.rowCount; j++){
            row.push({char:'-'});
        }
        $scope.rows.push(row);
    }

    for( var i=0; i < 26; i++ ){
        $scope.characters.push( {char:String.fromCharCode( i+65 )} );
    }
    for( var i=1; i <= Math.pow($scope.rows.length,2); i++ ){
        $scope.lengthCounter.push(i);
    }
     $timeout(function(){
        $('select:first').focus();
     });
    };

    $scope.init();

    $scope.moveIt = function(evt){
        console.log( evt.keyCode );
        var inp = String.fromCharCode(evt.keyCode);
        console.log( inp );
        if (/[a-zA-Z-]/.test(inp)){
            if( $scope.focusableInputs == null || $scope.focusableInputs.length==0)
            {
                $scope.focusableInputs  = $('select');
            }

            for(var i = 0; i < $scope.focusableInputs.length - 1; ++i) {
                    if($scope.focusableInputs[i] == evt.target)
                    {
                        console.log("focus next");
                        $scope.focusableInputs[i + 1].focus();
                        break;
                    }
            }
        }
    };

    $scope.solvePuzzle = function(){
        console.log( "rows: "+ $scope.rows );
        $http.get('solve',{ params:{
        rows: JSON.stringify($scope.rows),
        length: $scope.lengthNeeded
        }
        }).then( function(data){
            //TODO return results
            console.log("response:" + data);
        });
    };

});