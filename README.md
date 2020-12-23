# SpringyScores
Spring API project

As per the instructions, the versions for the tech stack:
- Java: 8
- Gradle: 6.5.1
- Spring/SpringBoot: 2.3.3
- Database (h2): 1.4.200
- JUnit: 5.6.2

1. How to build and run the app
- Download a zip file from the branch (and unzip the contents) or clone the branch wherever you'd like. If you'd prefer to use
an IDE, like intellij, to run it, open the "complete" folder in the project. If you'd like to use a shell to run it, open a an instance
of that shell in the "complete" folder.
- To run in an IDE, simply press run
- To run in a shell, type "./gradlew bootRun" and press enter

2. How to run tests
- Download a zip file from the branch (and unzip the contents) or clone the branch wherever you'd like. If you'd prefer to use
an IDE, like intellij, to run it, open the "complete" folder in the project. If you'd like to use a shell to run it, open a an instance
of that shell in the "complete" folder.
- To run tests in an IDE, right-click the "test" directory and press "Run Tests"
- To run tests in a shell, type "./gradlew test"
Both the above test commands will run all the test files, both integration and unit tests.
Integration tests found in: ScoreControllerIT and ScoreRepositoryIT (just one simple IT test for the repo)
Unit tests found in: ScoreControllerTest and ScoreServiceTest

3. Quick Documentation of the APIs
NOTE: localhost:8080 is the base URI for all the following APIs
- /createscore
	+ POST
	+ Used to create a new score for a player
	+ Returns: the newly saved score
	+ JSON payload will look like the following:

	{
    	"player": "khurl",
    	"score": 1500,
    	"time": "2020-12-24"
	}

	+ And returned JSON will look like the following:

	{
    	"id": 1,
    	"player": "khurl",
    	"score": 1500,
    	"time": "2020-12-24T00:00:00.000+00:00"
	}

	Note the returned Id. Decided to automatically generate the value using the Spring built in generation of (strategy=GenerationType.AUTO) which simply iterates values starting at 1.
	UUID generation was an alternative option.

- /{id}
	+ GET. Replace {id} above with the Id of the user score you'd like to retrieve
	+ Used to retrieve a previously saved score using the returned Id from a create
	+ Returns: the previously saved player score. Otherwise, a simple string stating: "User score doesn't exist"
	+ Returned JSON will like the following:

	{
    	"id": 1,
    	"player": "khurl",
    	"score": 1500,
    	"time": "2020-12-24T00:00:00.000+00:00"
	}

- /{id}
	+ DELETE. Replace {id} above with the Id of the user score you'd like to delete
	+ Used to delete a previously saved score using the returned Id from a create
	+ Returns: a simple string stating "Record deleted" if the record was deleted. "User score doesn't exist" otherwise

- /history/{player}
	+ GET
	+ Used to get player history. History includes: lowest score, top score, list of all scores of this player, and the average score of this player
	Note that "score" includes the score value and date
	+ Returns: the score history including all of the fields listed above. Simple string stating "User has no scores" otherwise
	+ Returned JSON will look like the following:

	{
	    "lowScore": 
	    {
	        "score": 1500,
	        "time": "2020-12-22T00:00:00.000+00:00"
	    },
	    "topScore": 
	    {
	        "score": 9000,
	        "time": "2020-12-24T00:00:00.000+00:00"
	    },
	    "averageScore": 4333.333333333333,
	    "playerScores": 
	    [
	        {
	            "score": 1500,
	            "time": "2020-12-22T00:00:00.000+00:00"
	        },
	        {
	            "score": 2500,
	            "time": "2020-12-23T00:00:00.000+00:00"
	        },
	        {
	            "score": 9000,
	            "time": "2020-12-24T00:00:00.000+00:00"
	        }
	    ]
	}

- /list?{various variables}
	+ GET
	+ Used to get combinations of data, with pagination supported. Possible request params are as follows:
		+ "players": Supports one or many. Would look like players=khurl for one or players=khurl,dan for multiple. Of course, no spaces!
		+ "datebefore": Request records before the specified date. Date requested should be in the form "yyyy-MM-dd." For example, datebefore=2020-12-24
		NOTE: the JPA method of finding records Before INCLUDES the day of request as well. i.e. if requested before 2020-12-24, it will be inclusive to that day.
		This could be fixed with vanilla SQL (see below!)
		+ "dateafter": Request records after the specified date. Date requested should be in the form "yyyy-MM-dd." For example, dateafter=2020-12-21
		+ "pageno": Page number for pagination. Would look like: "pageno=0"
		NOTE: because the directions stated that pagination MUST be supported, it was assumed that any request to this API must include pagination, so pageno must be a request param
		+ "pagesize": the number of records to return per page. Would look like: "pagesize=5"
		NOTE: like pageno, because the directions stated that pagination MUST be supported, it was assumed that any request to this API must include pagination, so pagesize must be a request param
	+ As a full example, if the user wanted records from two users between two dates with pagination, the full request params would look like the following:
		+ /list?players=khurl,dan&datebefore=2020-12-24&dateafter=2020-12-21&pageno=0&pagesize=3
		NOTE: postman allows request params to be easily added and removed under the params tab
	+ Returned JSON will look like the following:

	[
	    {
	        "id": 3,
	        "player": "khurl",
	        "score": 2400,
	        "time": "2020-12-21T00:00:00.000+00:00"
	    },
	    {
	        "id": 4,
	        "player": "khurl",
	        "score": 300,
	        "time": "2020-12-22T00:00:00.000+00:00"
	    },
	    {
	        "id": 5,
	        "player": "dan",
	        "score": 500,
	        "time": "2020-12-22T00:00:00.000+00:00"
	    }
	]

	NOTE: the above request was: /list?players=khurl,dan&datebefore=2020-12-23&dateafter=2020-12-20&pageno=0&pagesize=3

4. Ways the system could be improved
	- Data filtering: the JSON payloads coming into the endpoints are largely unchecked for invalid data, due to this just being a small sample project. In a production environement, that
	would certainly, and easily, be fixed
	- Protected endpoints: All of the endpoints are free for all and can be accessed by any call to them. Again, in a production environment, that will need addressed
	- JPA and vanilla SQL: For this project, I opted to use the built in Java Persistence API, which allows for making queries in your repository class using entity fields and keywords. Although
	this is nice and makes for less and more easily read code, there are limitations for what can be done for queries (See "datebefore" bullet in the /list documentation.) Vanilla SQL, though wordier,
	allows for more freedom. SQL queries can be created on JPA repository methods by using the @Query annotation. Example: @Query({the query here})
	- Data access conventions: for one thing, I named the entities models, but I believe the proper terminology to be DTO and DAO. For that matter, my models, or what would be named DTO, should not touch
	business logic. Again, for the sake of a sample project, I bypassed these usual conventions
	- More tests: easy one. More tests are always welcome. I feel as if I covered all the necessary code, as per the instructions, but there are certainly more cases that could potentially arise, edge case or otherwise