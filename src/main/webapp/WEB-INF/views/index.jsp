<html ng-app="woodBarnApp">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<script src="resources/lib/jquery-2.2.3.min.js"></script>
	<script src="resources/lib/angular.min.js"></script>
	<script src="resources/js/application.js"></script>
	<link rel="stylesheet" href="resources/styles/bootstrap/3.3.5/css/bootstrap.min.css" />
    <link rel="stylesheet" href="resources/styles/bootstrap/3.3.5/css/bootstrap-theme.min.css" />
	<title>Word Barn Solver</title>
</head>

<body>
<h1>Wood Barn Solver</h1>
 	<div class="container" ng-controller="WoodBarnController">
		<table class="table">
		<tr ng-repeat="row in rows track by $index">
		    <td ng-repeat="item in row track by $index">
		        <select ng-options="char.char as char.char for char in characters" ng-model="item.char" ng-keypress="moveIt($event)"></select>
		    </td>
		</tr>

		</table>
        <label>Length of Word Needed: <select ng-options="i as i for i in lengthCounter" ng-model="lengthNeeded"></select></label>
		<button class="btn" ng-click="solvePuzzle()">Solve It</button>
	</div>
</body>

</html>