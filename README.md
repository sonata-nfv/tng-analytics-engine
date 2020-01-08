[![Build Status](https://jenkins.sonata-nfv.eu/buildStatus/icon?job=tng-api-gtw/master)](https://jenkins.sonata-nfv.eu/job/tng-profiler)
[![Join the chat at https://gitter.im/5gtango/tango-schema](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sonata-nfv/5gtango-sdk)

# tng-analytics-engine component
This is a 5GTANGO component that aims to provide insights for the efficiency of the developed VNFs and NSs with regards to resources usage, elasticity efficiency and performance aspects. 

<p align="center"><img src="https://github.com/sonata-nfv/tng-api-gtw/wiki/images/sonata-5gtango-logo-500px.png" /></p>

## Installing / Getting started

This component is implemented in spring boot framework.boot 2.1.7.RELEASE, python flask 1.0.2 and opencpu 2.1 

### Installing from code

The tng-analytics-engine is composed from four microservices. The tng-analytics-engine, the tng-analytics-rserver, the tng-analytics-pserver and the tng-analytics-results.
tng-analytics-engine consists of the microservice than manages the creation and registration of new analytics services and the execution of analytic processes. The tng-analytics-pserver and tng-analytics-pserver host and execute packages implemented in python and R statistical languages.tng-analytics-results stores the analytic results and makes them available to the end user via the tng-analytics-engine.    
To have up and running then tng-analytics-engine from code, please do the following:
```  
$ git clone https://github.com/sonata-nfv/tng-analytics-engine.git # Clone this repository
$ cd tng-analytics-engine# Go to the newly created folder
$ mvn clean install # Install dependencies
$ java -jar target/tng-analytics-engine-0.0.1-SNAPSHOT.jar # use the development profile or create a new one
```  
Note: See the [Configuration](https://github.com/sonata-nfv/tng-analytics-engine/#configuration) section below for other environment variables that can be used.

Everything being fine, you'll have a server running on that session, on port 8083. You can access it by using curl, like in:
```
$ curl <host name>:8085/api/vi
```
To have up and running then tng-analytics-rserver from code, please do the following:
* Install the [R Project](https://www.r-project.org/) for Statistical Computing mininum version (R version 3.4.4 (2018-03-15))
* Install the [Opencpu server](https://www.opencpu.org/) for Embedded Scientific Computing 
* Install the [rstudio](https://www.rstudio.com/) in case you want to create a new analytics package
* Install the [Physiognomica](https://github.com/ubitech/Physiognomica) R package
```
# Requires Ubuntu 18.04 (Bionic) or 16.04 (Xenial)
sudo add-apt-repository -y ppa:opencpu/opencpu-2.1
sudo apt-get update 
sudo apt-get upgrade

# Installs OpenCPU server
sudo apt-get install -y opencpu-server
# Done! Open http://yourhost/ocpu in your browser

# Optional: installs rstudio in http://yourhost/rstudio
sudo apt-get install -y rstudio-server 

sudo apt-get install pandoc -y
R 
>>install.packages(c('devtools','xts','fpp2','scatterD3','dplyr','usdm','psych','shiny','tableHTML','sjPlot','MASS','corrplot','stringr','jsonlite'),repos='http://cran.rstudio.com/')
>>devtools::install_github('mattflor/chorddiag')
>>devtools::install_github('ubitech/Physiognomica',force=TRUE)
```

To have up and running then tng-analytics-pserver from code, please do the following:
```  
$ cd tng-analytics-engine/tng-analytics-pserver 
$ pip3 install --upgrade pip && pip install -r requirements.txt
$ python3 ./app.py
```  
tng-analytics-results is just an apache2 server 

### Installing from the Docker container

In case you prefer a docker based development, you can run the following commands (bash shell):
```
  $docker-compose up --build -d
```
With these commands, you:

    Run the MongoDB container within the tango network;
    Run the tng-analytics-engine container within the tango network;
    Run the tng-analytics-rserver container
    Run the tng-analytics-pserver container
    Run the tng-analytics-results container
    
## Developing

To contribute to the development of this 5GTANGO component, you may use the very same development workflow as for any other 5GTANGO Github project. That is, you have to fork the repository and create pull requests.

### Built With

* Sonata Service Platform local installation (recommended) or vpn connection to SP environment 
* [Docker >= 1.13](https://www.docker.com/)
* [Docker compose version 3](https://docs.docker.com/compose/)
* [Java version 1.8](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - The programming language used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Drools version 7.7.0](https://www.drools.org/) - Business Rules Management System (BRMS) solution used so as to enforce policies
* [Spring boot Framework 2.0.3 RELEASE](https://spring.io/projects/spring-boot) - Used application framework
* [R Project](https://www.r-project.org/)
* [Opencpu server](https://www.opencpu.org/) 
* [Pyhton Flask](https://palletsprojects.com/p/flask/) 

### Prerequisites
Be sure the apparmor-utils installed at the host machine in case you run the tng-analytics-engine as container
```
sudo apt-get install apparmor-utils
```  
### Setting up Dev

Developing this micro-service is straightforward with a low amount of necessary steps:

*  Update properly the application.properties and / of docker-compose.yml files
*  Open the tng-analytics-engine with an Integrated development environment (IDE) that support java (ex.Netbeans)

### Submiting changes
Changes to the repository can be requested using [this](https://github.com/sonata-nfv/tng-analytics-engine/issues) repository's issues and [pull requests](https://github.com/sonata-nfv/tng-analytics-engine/pulls) mechanisms.

## Versioning
The most up-to-date version is v5.0. For the versions available, see the [link](https://github.com/sonata-nfv/tng-analytics-engine/releases) to tags on this repository.

## Configuration

The configuration of the micro-service is done through the following environment variables, defined in the Dockerfile:
  
* MONGO_DB, which defines the mongo database, where all necessary objects are stored in;  
* PHYSIOG_URL,  which defines the  R analytics server where the R packages are hosted are executed;  
* ENV PANALYTICS_URL, which defines the  python analytics server where the python packages are hosted are executed;  
* ENV MONITORING_ENGINE, which defines where the monitoring metrics are stored and how to fech them;  
* ENV PROMETHEUS_URL, which defines where the monitoring metrics are stored and how to fech them;  
* ENV REPOSITORY_URL, which defines the repository where the VnV test results are stored;  

## Tests
Unit tests are automatically executed during the building of the microsevice. 
Integration and functional tests involving this micro-service are defined in [tng-tests](https://github.com/sonata-nfv/tng-tests) repository:
* [Check that analytics engine is correctly integrated at VnV environment.](https://github.com/sonata-nfv/tng-tests/tree/master/tests/VnV/test_analytic_engine)

## Style guide
Our style guide is really simple:

* We try to follow a Clean Code philosophy in as much as possible, i.e., classes and methods should do one thing only, have the least number of parameters possible, etc.;

## Api Reference 

Analytic Engine APIs can be found at the  [central API documentation of the SONATA orchestrator](https://sonata-nfv.github.io/tng-doc/?urls.primaryName=5GTANGO%20ANALYTICS%20ENGINE%20REST%20API) 

## Licensing

This 5GTANGO component is published under Apache 2.0 license. Please see the LICENSE file for more details.

#### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.

- Eleni Fotopoulou ([@elfo](https://github.com/efotopoulou))
- Anastasios Zafeiropoulos ([@tzafeir ](https://github.com/azafeiropoulos))

#### Feedback-Chanel
* You may use the mailing list [sonata-dev-list](mailto:sonata-dev@lists.atosresearch.eu)
* You may use the GitHub issues to report bugs
* Gitter room [![Join the chat at https://gitter.im/5gtango/tango-schema](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sonata-nfv/5gtango-vnv)

* Please use the GitHub issues to report bugs.



