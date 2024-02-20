@Library(value='ibp-libraries', changelog=false) _

def config = [:]

node {
    checkout scm
    config = readYaml file: "library-config.yaml"
    config["pod_label"] = jobPodLabel()
}

pipeline {
   agent {
     kubernetes {
        label "${config.pod_label}"
        defaultContainer "maven"
        yaml """
          apiVersion: v1
          kind: Pod
          spec:
              containers:
              - name: maven
                image: 'docker.intuit.com/${config.mavenImage}'
                command:
                - cat
                tty: true
        """
      }
   }
   
   post {
       always {
           customReleaseMetrics(config)
       }
   }
    environment {
        IBP_MAVEN_SETTINGS_FILE = credentials("${config.mavenSettingsFileId}")
        MAVEN_ARTIFACTORY_CREDENTIALS = credentials("${config.artifactoryCredentialsId}")
        MAVEN_ARTIFACTORY_USERID = "${env.MAVEN_ARTIFACTORY_CREDENTIALS_USR}"
        MAVEN_ARTIFACTORY_TOKEN = "${env.MAVEN_ARTIFACTORY_CREDENTIALS_PSW}"
        GIT_BRANCH = "${env.BRANCH_NAME}"
    }

 parameters {
    choice(
      name: 'type',
      choices: ['Snapshot', 'Release'],
      description: 'Choose build type "snapshot" or "release" (ignored for PR builds!)')
  }

  stages {

    stage('✅ PR:') {
        when { changeRequest() }
        steps {
          echo 'Compile your library'
          wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'xterm']) {
            catchError {
              mavenBuildPR("-U -B -s settings.xml")
              }
           }
         }
         post {
           success {
             PRPostSuccess(config)
           }
           always {
             PRPostAlways(config)
           }
         }
    }

     stage('CI') {
          when {
            allOf {
              not { changeRequest() }
              expression { 'Snapshot' == params.type }
              branch 'master'
            }
          }
          steps {
               mavenBuildCI("-U -B -s settings.xml")
          }
          post {
            success {
              CIPostSuccess(config)
            }
            always {
              CIPostAlways(config)
            }
          }
        }

    stage('Release') {
      when {
        allOf {
          not { changeRequest() }
          expression { 'Release' == params.type }
        }
      }
      steps {
        mavenBuildRelease(config,"-U -B -s settings.xml")
      }
      post {
        success {
          CIPostSuccess(config)
        }
        always {
          CIPostAlways(config)
        }
      }
    }
  }
}
