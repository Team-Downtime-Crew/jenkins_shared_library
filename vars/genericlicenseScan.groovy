def call() {
    stage('Install FOSSA CLI') {
        sh '''
            curl -H 'Cache-Control: no-cache' https://raw.githubusercontent.com/fossas/fossa-cli/master/install-latest.sh | bash
            fossa --version
        '''
    }

    stage('FOSSA Init') {
        withCredentials([string(credentialsId: 'fossa-api-key', variable: 'FOSSA_API_KEY')]) {
            sh '''
                export FOSSA_API_KEY=$FOSSA_API_KEY
                fossa init
                if [ -f .fossa.yml ]; then
                    sed -i "s/^version: 2/version: 3/" .fossa.yml
                fi
            '''
        }
    }

    stage('FOSSA Analyze') {
        withCredentials([string(credentialsId: 'fossa-api-key', variable: 'FOSSA_API_KEY')]) {
            sh 'export FOSSA_API_KEY=$FOSSA_API_KEY && fossa analyze'
        }
    }

    stage('FOSSA Test') {
        withCredentials([string(credentialsId: 'fossa-api-key', variable: 'FOSSA_API_KEY')]) {
            sh 'export FOSSA_API_KEY=$FOSSA_API_KEY && fossa test'
        }
    }
}
