pipeline {
    agent any

    environment {
        APP_NAME       = 'electronics-store'
        DOCKER_IMAGE   = "fallousenghor/electronics-store"
        DOCKER_TAG     = "${BUILD_NUMBER}"
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    tools {
        maven 'Maven-3.9'
        jdk   'JDK-17'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    stages {

        // ── 1. Récupération du code ─────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        // ── 2. Build Maven ──────────────────────────────────────────────────
        stage('Build') {
            steps {
                echo '🔨 Build Maven...'
                dir('project/backend') {
                    sh 'mvn clean compile -B -q'
                }
            }
        }

        // ── 3. Tests unitaires ──────────────────────────────────────────────
        stage('Tests') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                dir('project/backend') {
                    sh 'mvn test -B'
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

        // ── 4. Analyse qualité SonarQube ────────────────────────────────────
        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyse SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    dir('project/backend') {
                        sh """
                            mvn sonar:sonar \
                              -Dsonar.projectKey=${APP_NAME} \
                              -Dsonar.projectName='Electronics Store Backend' \
                              -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                              -B -q
                        """
                    }
                }
            }
        }

        // ── 5. Packaging JAR ────────────────────────────────────────────────
        stage('Package') {
            steps {
                echo '📦 Packaging de l\'application...'
                dir('project/backend') {
                    sh 'mvn package -DskipTests -B -q'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ── 6. Build image Docker ───────────────────────────────────────────
        stage('Docker Build') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
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

        // ── 7. Push DockerHub ───────────────────────────────────────────────
        stage('Push DockerHub') {
            when { branch 'main' }
            steps {
                echo '📤 Push vers DockerHub...'
                script {
                    docker.withRegistry('', 'dockerhub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        // ── 8. Déploiement local ────────────────────────────────────────────
        stage('Deploy') {
            when { branch 'main' }
            steps {
                echo '🚀 Déploiement local via docker-compose...'
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