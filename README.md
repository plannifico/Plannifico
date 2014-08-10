Plannifico
==========

The first open source planning, budgeting and forecasting system based on an open and modular java-based platform.

Plannifico key goals are:
* Create and analyze sophisticated plans, budgets and forecasts, based on large data sets and complex logics
* Enable what-if analysis and scenarios simulation on unlimited dimensions (e.g. product, customer)
* Enable driver-based planning and simulations 
* Combine bottom-up and top-down planning
* Support easy data-integration
* Scale to millions of records

Find further information <a href="http://plannifico.github.io/" target="_blank">here</a>

Version 0.2
-----------

* Minor APIs fixes
 
Version 0.3
-----------

* Added method "GetDataset" to the APIs
 

Components
----------

* Plannifico-Server:  Plannifico calculation engine
* Plannifico-App: Plannifico client console


Running
-------

Prerequisite: ensure that a java versions >= 1.7 is installed on your computer (open a command line and run ">java -version" command)

Start running a quick demo:

* Download Plannifico <a href="https://github.com/plannifico/Plannifico/tree/master/dist">here</a>
* Unzip it into a preferred folder (e.g. plannifico-server-0.1-SNAPSHOT)
* Open a command line console
* Go into the unzipped folder (e.g. ">cd plannifico-server-0.1-SNAPSHOT") from your command line
* Run the demo in order to double check that everything works fine:
    * Run the following command ">demo\create_demo_db.bat" in order to create a demo DB
    * Run the following command ">run.bat" in order to start the http listener
    * Wait until the following lines are displyed: "INFO: Initiating Jersey application, version Jersey:..."
    * Go into the folder "demo" and open the file "demo_command.html" from your preferred browser"
    * Click on the "start" link and wait until the following json response appears: "{"statusCode":"0","statusDescription":"Success: server started","content":null}"
    * Run other commands in order to explore Plannifico features
    * See the <a href="https://github.com/plannifico/Plannifico/wiki/RESTful Web APIs">RESTful Web APIs</a> documentation in order to run further commands
    * enjoy!!!

Documentation
-------------
* <a href="https://github.com/plannifico/Plannifico/wiki/Basic-Concepts---Overall-Design">Basic Concepts</a>

* <a href="https://github.com/plannifico/Plannifico/wiki/RESTful Web APIs">RESTful Web APIs</a>

License
-------

<a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License, Version 2.0</a>
