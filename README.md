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
 
Components
----------

* Plannifico-Server:  Plannifico calculation engine
* Plannifico-Console: Plannifico administration and calculation console
* Plannifico-Client:  Plannifico calculation client

Running
-------

Prerequisite: ensure java >= 1.7 is installed on your computer (open a command line and run ">java -version" command)

* Download Plannifico <a href="https://github.com/plannifico/Plannifico/tree/master/dist">here</a>
* Unzip it into a preferred folder (e.g. plannifico-server-0.1-SNAPSHOT)
* Open command line console
* Go into the unzipped folder (e.g. cp plannifico-server-0.1-SNAPSHOT) from your command line
* Run the demo in order to double check that everything works fine
    * Run the following command ">demo\create_demo_db.bat"
    * Run the following command ">run.bat"
    * Wait until the following lines are displyed: "INFO: Initiating Jersey application, version Jersey:..."
    * Go into the folder demo and open the file "demo_command.html" from your preferred browser"
    * Try to click on the "start" link and wait until the following json response appears: "{"statusCode":"0","statusDescription":"Success: server started","content":null}"
    * enjoy!!!

Documentation
-------------
<a href="https://github.com/plannifico/Plannifico/wiki/Basic-Concepts---Overall-Design">Basic Concepts</a>

License
-------

<a href="http://www.apache.org/licenses/LICENSE-2.0.html">Apache License, Version 2.0</a>
