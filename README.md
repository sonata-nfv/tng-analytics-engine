[![Build Status](https://jenkins.sonata-nfv.eu/buildStatus/icon?job=tng-api-gtw/master)](https://jenkins.sonata-nfv.eu/job/tng-profiler)
[![Join the chat at https://gitter.im/5gtango/tango-schema](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sonata-nfv/5gtango-sdk)

# tng-analytics-engine component
This is a 5GTANGO component that aims to provide insights for the efficiency of the developed VNFs and NSs with regards to resources usage, elasticity efficiency and performance aspects. 

<p align="center"><img src="https://github.com/sonata-nfv/tng-api-gtw/wiki/images/sonata-5gtango-logo-500px.png" /></p>

## Installing / Getting started

This component is implemented in spring boot framework.boot, 2.1.7.RELEASE

### Installing from code

The tng-analytics-engine is composed from two microservices. The tng-analytics-engine and the tng-analytics-rserver.
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
* Install the [R Project](https://www.r-project.org/) for Statistical Computing mininum version (R version 3.4.4 (2018-03-15)) https://www.r-project.org/
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
###Installing from the Docker container

In case you prefer a docker based development, you can run the following commands (bash shell):
```
  $docker-compose up --build -d
```
With these commands, you:

    Run the MongoDB container within the tango network;
    Run the tng-analytics-engine container within the tango network;
    Run the tng-analytics-rserver container

### Documentation
For get informed about how the Analytics Engine is interacting with other 5GTango components and what kind of APIs it exposes, please visit the wiki page: https://github.com/sonata-nfv/tng-analytics-engine/wiki

### Prerequisites
Be sure the apparmor-utils installed at the host machine in case you run the tng-analytics-engine as container
```
sudo apt-get install apparmor-utils
```  

### CI Integration
All pull requests are automatically tested by Jenkins and will only be accepted if no test is broken.

### License

This 5GTANGO component is published under Apache 2.0 license. Please see the LICENSE file for more details.

### Lead Developers

The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.

- Eleni Fotopoulou ([@elfo](https://github.com/efotopoulou))
- Anastasios Zafeiropoulos ([@azafeiropoulos](https://github.com/azafeiropoulos))

### Feedback-Chanel

* Please use the GitHub issues to report bugs.
