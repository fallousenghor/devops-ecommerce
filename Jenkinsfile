pipeline {
    agent any

    environment {
        APP_NAME       = 'electronics-store'
        DOCKER_IMAGE   = "fallousenghor/electronics-store"
        DOCKER_TAG     = "${BUILD_NUMBER}"
        SONAR_HOST_URL = 'http://sonarqube:9000'
        MVN            = '/usr/bin/mvn'
    }

    // ← PAS de bloc tools

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    stages {

        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Build Maven...'
                dir('project/backend') {
                    sh '${MVN} clean compile -B -q'
                }
            }
        }

        stage('Tests') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                dir('project/backend') {
                    sh '${MVN} test -B'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'project/backend/target/surefire-reports/**/*.xml'
                }
                failure {
                    echo '❌ Tests échoués!'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyse SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    dir('project/backend') {
                        sh """
                            ${MVN} sonar:sonar \
                              -Dsonar.projectKey=${APP_NAME} \
                              -Dsonar.projectName='Electronics Store Backend' \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -B -q
                        """
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo '📦 Packaging...'
                dir('project/backend') {
                    sh '${MVN} package -DskipTests -B -q'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Build Docker...'
                dir('project/backend') {
                    sh """
                        docker build \
                          -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                          -t ${DOCKER_IMAGE}:latest \
                          .
                    """
                }
            }
        }

        stage('Push DockerHub') {
            when { branch 'main' }
            steps {
                echo '📤 Push DockerHub...'
                script {
                    docker.withRegistry('', 'dockerhub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        stage('Deploy') {
            when { branch 'main' }
            steps {
                echo '🚀 Déploiement...'
                sh """
                    docker-compose -f project/docker-compose.yml down backend || true
                    docker-compose -f project/docker-compose.yml up -d backend
                """
                echo '✅ Déploiement terminé!'
            }
        }
    }

    post {
        success {
            echo """
            ╔════════════════════════════════════╗
            ║  ✅ Pipeline réussi!               ║
            ║  Build #${BUILD_NUMBER}            ║
            ╚════════════════════════════════════╝
            """
        }
        failure {
            echo """
            ╔════════════════════════════════════╗
            ║  ❌ Pipeline échoué!               ║
            ║  Build #${BUILD_NUMBER}            ║
            ╚════════════════════════════════════╝
            """
        }
        always {
            cleanWs()
        }
    }
}