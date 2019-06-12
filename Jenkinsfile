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
	   stage('Deploying to integration') {
		  when{
			  branch 'master'
		  }
		  steps {
        sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-engine:latest registry.sonata-nfv.eu:5000/tng-analytics-engine:int'
        sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-engine:int'
        sh 'docker tag registry.sonata-nfv.eu:5000/tng-analytics-rserver:latest registry.sonata-nfv.eu:5000/tng-analytics-rserver:int'
        sh 'docker push registry.sonata-nfv.eu:5000/tng-analytics-rserver:int'
        sh 'rm -rf tng-devops || true'
        sh 'git clone https://github.com/sonata-nfv/tng-devops.git'
        dir(path: 'tng-devops') {
          sh 'ansible-playbook roles/vnv.yml -i environments -e "target=int-vnv component=analytics"'
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
