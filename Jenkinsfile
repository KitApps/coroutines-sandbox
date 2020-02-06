#!/usr/bin/env groovy

pipeline {
    agent any

    parameters {

        choice(
                name: 'service_host',
                choices: ['NONE', 'NIO', 'BLOCKING'],
                description: 'service to be loaded'
        )

        string(name: 'load_test_duration', defaultValue: '300', description: 'Simulation duration in seconds')

    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "./gradlew clean build -x test"
            }
        }

        stage('Load-test') {
            when {
                expression { params.service_host != 'NONE' }
            }

            environment {
                //                                                              nio             blocking
                SERVICE_INSTANCE = "http://${params.service_host == 'NIO' ? '172.31.2.175' : '172.31.22.136'}:8080"

                SIMULATION_DURATION = "${params.load_test_duration}"
            }

            steps {
                echo sh(script: 'env|sort', returnStdout: true)

                sh "./gradlew gatlingRun -PsimulationName=SIMULATION"
                gatlingArchive()
            }
        }
    }
}
