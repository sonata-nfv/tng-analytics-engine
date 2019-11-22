pipeline {
  agent any
  stages {
    stage('Container Build analytics') {
      parallel {
        stage('Container Build') {
          steps {
            echo 'Building...'
          }
        }
        stage('Building tng-analytics-engine') {
          steps {
            sh 'docker build -t registry.sonata-nfv.eu:5000/tng-analytics-engine .'
          }
        }
        stage('Building tng-analytics-rserver') {
          steps {
            sh 'docker build -t registry.sonata-nfv.eu:5000/tng-analytics-rserver -f tng-analytics-rserver/Dockerfile tng-analytics-rserver/'
          }
        }
        stage('Building tng-analytics-results') {
          steps {
            sh 'docker build -t registry.sonata-nfv.eu:5000/tng-analytics-results -f tng-analytics-results/Dockerfile tng-analytics-results/'
          }
        }
      }
    }
    stage('Containers Publication') {
      parallel {
        stage('Containers Publication') {
          steps {
            echo 'Publication of containers in local registry....'
          }
        }
        stage('Publishing tng-analytics-engine') {
          steps {
            sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-engine'
          }
        }
        stage('Publishing tng-analytics-rserver') {
          steps {
            sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-rserver'
          }
        }
        stage('Publishing tng-analytics-results') {
          steps {
            sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-results'
          }
        }
      }
    }
    stage('Deployment in VnV pre-int') {
      parallel {
        stage('Deployment in VnV pre-int') {
          steps {
            echo 'Deploying in VnV pre-int...'
          }
        }
        stage('Deploying') {
          steps {
            sh 'rm -rf tng-devops || true'
            sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
            dir(path: 'tng-devops') {
              sh 'ansible-playbook roles/vnv.yml -i environments -e "target=pre-int-vnv component=analytics"'
            }
          }
        }
      }
    }
    stage('Promoting containers to integration env') {
      when {
         branch 'master'
      }
      parallel {
        stage('Publishing containers to int') {
          steps {
            echo 'Promoting containers to integration'
          }
        }
        stage('tng-analytics-engine') {
          steps {
            sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-engine:latest registry.sonata-nfv.eu:5000/tng-analytics-engine:int'
            sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-engine:int'
          }
        }
        stage('tng-analytics-rserver') {
          steps {
            sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-rserver:latest registry.sonata-nfv.eu:5000/tng-analytics-rserver:int'
            sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-rserver:int'
          }
        }
        stage('tng-analytics-results') {
          steps {
            sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-results:latest registry.sonata-nfv.eu:5000/tng-analytics-results:int'
            sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-results:int'
          }
        }
	   stage('Deploying to integration') {
		  when{
			  branch 'master'
		  }
		  steps {
        sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-engine:latest registry.sonata-nfv.eu:5000/tng-analytics-engine:int'
        sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-engine:int'
        sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-rserver:latest registry.sonata-nfv.eu:5000/tng-analytics-rserver:int'
        sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-rserver:int'
        sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-results:latest registry.sonata-nfv.eu:5000/tng-analytics-results:int'
        sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-results:int'
        sh 'rm -rf tng-devops || true'
        sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
        dir(path: 'tng-devops') {
          sh 'ansible-playbook roles/vnv.yml -i environments -e "target=int-vnv component=analytics"'
			  }
		  }
		}

      }
    }
    stage('Promoting release v5.0') {
        when {
            branch 'v5.0'
        }
        stages {
            stage('Generating release') {
                steps {
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-engine:latest registry.sonata-nfv.eu:5000/tng-analytics-engine:v5.0'
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-engine:latest sonatanfv/tng-analytics-engine:v5.0'
                    sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-engine:v5.0'
                    sh 'docker push sonatanfv/tng-analytics-engine:v5.0'
                    
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-rserver:latest registry.sonata-nfv.eu:5000/tng-analytics-rserver:v5.0'
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-rserver:latest sonatanfv/tng-analytics-rserver:v5.0'
                    sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-rserver:v5.0'
                    sh 'docker push sonatanfv/tng-analytics-rserver:v5.0'
                    
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-results:latest registry.sonata-nfv.eu:5000/tng-analytics-results:v5.0'
                    sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-results:latest sonatanfv/tng-analytics-results:v5.0'
                    sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-results:v5.0'
                    sh 'docker push sonatanfv/tng-analytics-results:v5.0'
                }
            }
            stage('Deploying in v5.0 servers') {
                steps {
                    sh 'rm -rf tng-devops || true'
                    sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
                    dir(path: 'tng-devops') {
                    sh 'ansible-playbook roles/sp.yml -i environments -e "target=sta-sp-v5-0 component=analytics"'
                    sh 'ansible-playbook roles/vnv.yml -i environments -e "target=sta-vnv-v5-0 component=analytics"'
                    }
                }
            }
        }
    }
  }
  post {
    always {
      echo 'TODO'
    }
  }
}
