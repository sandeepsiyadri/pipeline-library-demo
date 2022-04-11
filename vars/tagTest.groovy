#!/usr/bin/env groovy

def call(String name = 'Sandeep Siyadri') {
  echo "Hello, ${name}."
  def TAG_VERSION = sh(returnStdout: true, script: "git tag --contains").trim()
  echo "${TAG_VERSION}"

  if("${env.BRANCH_NAME}".contains('feature')) {
    pipeline {

        agent any

        options {
            buildDiscarder logRotator(
                        daysToKeepStr: '16',
                        numToKeepStr: '10'
                )
        }

        stages {

            stage('Cleanup Workspace') {
                steps {
                    cleanWs()
                    sh """
                    echo "Cleaned Up Workspace For Project"
                    """
                }
            }

            stage('Branch Checkout') {
                when {
                        expression{env.BRANCH_NAME == 'master'||'develop'||'feature'}
                }
                steps {
                        checkout([
                        $class: 'GitSCM',
                            branches: [[name: "${env.BRANCH_NAME}"]],
                            userRemoteConfigs: [[credentialsId: 'githubcred', url: 'https://github.com/sandeepsiyadri/multibranch-pipeline-demo.git']]
                        ])
                }
            }
            /* stage('Tag Checkout') {
                when {
                      tag "release-*"
                }
                steps {
                        script {
                            TAG_VERSION = sh(returnStdout: true, script: "git tag --contains").trim()
                            echo "tag version is ${TAG_VERSION}"
                        }
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: "refs/tags/${TAG_VERSION}"]],
                            userRemoteConfigs: [[credentialsId: 'githubcred', url: 'https://github.com/sandeepsiyadri/multibranch-pipeline-demo.git']]
                        ])
                }
            } */

            stage(' Unit Testing') {
                steps {
                    sh """
                    echo "Running Unit Tests"
                    """
                }
            }

            stage('Code Analysis') {
                steps {
                    sh """
                    echo "Running Code Analysis"
                    """
                }
            }

            stage('Build Deploy Code') {
                when {
                    branch 'develop'
                }
                steps {
                    sh """
                    echo "Building Artifact"
                    """

                    sh """
                    echo "Deploying Code"
                    """
                }
            }

        }
    }
  } else if ("${env.TAG_NAME}".contains('v')) {
    pipeline {

        agent any

        options {
            buildDiscarder logRotator(
                        daysToKeepStr: '16',
                        numToKeepStr: '10'
                )
        }

        stages {

            stage('Cleanup Workspace') {
                steps {
                    cleanWs()
                    sh """
                    echo "Cleaned Up Workspace For Project"
                    """
                }
            }
            stage('Tag Checkout') {
                steps {
                        /*script {
                            TAG_VERSION = sh(returnStdout: true, script: "git tag --contains").trim()
                            echo "tag version is ${TAG_VERSION}"
                        }*/
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: "refs/tags/${TAG_VERSION}"]],
                            userRemoteConfigs: [[credentialsId: 'githubcred', url: 'https://github.com/sandeepsiyadri/multibranch-pipeline-demo.git']]
                        ])
                }
            }

            stage(' Unit Testing') {
                steps {
                    sh """
                    echo "Running Unit Tests TAG"
                    """
                }
            }

            stage('Code Analysis') {
                steps {
                    sh """
                    echo "Running Code Analysis TAG"
                    """
                }
            }

            stage('Build Deploy Code') {
                when {
                    branch 'develop'
                }
                steps {
                    sh """
                    echo "Building Artifact TAG"
                    """

                    sh """
                    echo "Deploying Code TAG"
                    """
                }
            }

        }
    }
  }

}
