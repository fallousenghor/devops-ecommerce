pipeline {
    agent any

    environment {
        APP_NAME        = 'electronics-store'
        DOCKER_IMAGE    = "electronics-store/backend"
        DOCKER_TAG      = "${BUILD_NUMBER}"
        SONAR_TOKEN     = credentials('sonar-token')
        SONAR_HOST_URL  = 'http://sonarqube:9000'
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
                dir('backend') {
                    sh 'mvn clean compile -B -q'
                }
            }
        }

        // ── 3. Tests unitaires ──────────────────────────────────────────────
        stage('Tests') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                dir('backend') {
                    sh 'mvn test -B'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/**/*.xml'
                    jacoco(
                        execPattern: 'backend/target/jacoco.exec',
                        classPattern: 'backend/target/classes',
                        sourcePattern: 'backend/src/main/java'
                    )
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
                dir('backend') {
                    sh """
                        mvn sonar:sonar \
                          -Dsonar.projectKey=${APP_NAME} \
                          -Dsonar.projectName='Electronics Store Backend' \
                          -Dsonar.host.url=${SONAR_HOST_URL} \
                          -Dsonar.token=${SONAR_TOKEN} \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                          -B -q
                    """
                }
            }
        }

        // ── 5. Packaging JAR ────────────────────────────────────────────────
        stage('Package') {
            steps {
                echo '📦 Packaging de l\'application...'
                dir('backend') {
                    sh 'mvn package -DskipTests -B -q'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ── 6. Build image Docker ───────────────────────────────────────────
        stage('Docker Build') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                dir('backend') {
                    sh """
                        docker build \
                          -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                          -t ${DOCKER_IMAGE}:latest \
                          .
                    """
                }
            }
        }

        // ── 7. Déploiement local ────────────────────────────────────────────
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Déploiement local via docker-compose...'
                sh """
                    docker-compose down backend || true
                    docker-compose up -d backend
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
