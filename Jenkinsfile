pipeline {

    // =========================================================================
    // AGENT
    // Define ONDE o pipeline vai rodar.
    // 'any' = Jenkins escolhe qualquer nó disponível (o próprio servidor ou
    // um agente conectado). Em pipelines reais usa-se 'docker { image "..." }'
    // para garantir ambiente idêntico em toda execução.
    // =========================================================================
    agent any

    tools {
        maven 'Maven'
    }

    // =========================================================================
    // ENVIRONMENT
    // Variáveis de ambiente injetadas em todos os stages.
    // Ficam disponíveis como variáveis de shell: $HEADLESS, $RELATORIO_DIR etc.
    // =========================================================================
    environment {
        HEADLESS       = 'true'
        RELATORIO_DIR  = 'report'
        ROBOT_DIR      = 'robot-tests'
        ROBOT_OUTPUT   = 'robot-output'
    }

    // =========================================================================
    // OPTIONS
    // Configurações globais do job.
    // =========================================================================
    options {
        // Quanto tempo no máximo o pipeline inteiro pode rodar antes de ser
        // cancelado. Evita builds presos infinitamente.
        timeout(time: 20, unit: 'MINUTES')

        // Impede que dois builds do mesmo job rodem ao mesmo tempo.
        // Crítico para testes de browser: dois Chrome abrindo juntos podem
        // colidir por porta, display ou recursos do sistema.
        disableConcurrentBuilds()

        // Mantém somente os últimos N builds no histórico do Jenkins.
        // Economiza disco no servidor de CI.
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    // =========================================================================
    // STAGES
    // O coração do pipeline. Cada stage é uma fase lógica claramente nomeada.
    // O Jenkins exibe cada stage como um bloco colorido na interface, o que
    // facilita ver exatamente onde um build falhou.
    // =========================================================================
    stages {

        // ---------------------------------------------------------------------
        // STAGE 1 — CHECKOUT
        // No Jenkins rodando via SCM (GitHub, GitLab, Bitbucket), o código já
        // é clonado automaticamente antes dos stages. Este stage existe para
        // tornar explícito o que está acontecendo e logar a versão do commit,
        // facilitando rastreabilidade: "o build 42 testou o commit abc123".
        // ---------------------------------------------------------------------
        stage('Checkout') {
            steps {
                checkout scm
                sh 'echo "Branch: $GIT_BRANCH | Commit: $GIT_COMMIT"'
            }
        }

        // ---------------------------------------------------------------------
        // STAGE 2 — BUILD
        // Compila o código Java SEM rodar testes (-DskipTests).
        // Por que separar build de teste? Porque um erro de compilação é
        // diferente de um teste falhando. Se o build quebrar aqui, você sabe
        // que o problema é no código-fonte, não nos testes.
        // ---------------------------------------------------------------------
        stage('Build') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        // ---------------------------------------------------------------------
        // STAGE 3 — TESTES SELENIUM + CUCUMBER
        // Roda os testes Java com Maven. O parâmetro -Dheadless=$HEADLESS
        // permite que o TestRule leia a variável de ambiente e abra o Chrome
        // no modo headless (sem interface gráfica), o que é obrigatório em
        // servidores CI que não têm monitor.
        //
        // 'catchError' faz o stage marcar como UNSTABLE (amarelo) se os testes
        // falharem, mas deixa o pipeline continuar para os próximos stages.
        // Isso garante que o Robot Framework também rode e os relatórios
        // sejam publicados, mesmo que o Cucumber tenha falhas.
        // ---------------------------------------------------------------------
        stage('Testes Selenium + Cucumber') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    sh 'mvn test -Dheadless=${HEADLESS}'
                }
            }
            post {
                always {
                    // Arquiva o relatório HTML do ExtentReports como artefato
                    // do build. Fica disponível para download na interface
                    // do Jenkins, mesmo após o workspace ser limpo.
                    archiveArtifacts artifacts: "${RELATORIO_DIR}/**/*",
                                     allowEmptyArchive: true
                }
            }
        }

        // ---------------------------------------------------------------------
        // STAGE 4 — TESTES ROBOT FRAMEWORK
        // Robot Framework usa Python, por isso ativamos o virtualenv antes.
        // O 'robot' command gera output.xml, log.html e report.html.
        // Esses arquivos são lidos pelo plugin "Robot Framework" do Jenkins
        // para exibir resultados detalhados na interface (stage 5).
        //
        // '--outputdir' define onde os artefatos do Robot serão salvos.
        // '--variable' sobrescreve variáveis do config.robot em tempo de
        // execução — essencial para trocar o CHROMEDRIVER path no servidor CI.
        // ---------------------------------------------------------------------
        stage('Testes Robot Framework') {
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    sh """
                        cd ${ROBOT_DIR}
                        . ../.venv/bin/activate
                        robot \
                            --outputdir ../${ROBOT_OUTPUT} \
                            --variable HEADLESS:true \
                            tests/
                    """
                }
            }
        }

        // ---------------------------------------------------------------------
        // STAGE 5 — PUBLICAR RELATÓRIOS
        // Publica os resultados de forma legível na interface do Jenkins.
        //
        // publishHTML → usa o plugin "HTML Publisher". Publica o relatório
        // do ExtentReports como uma página navegável dentro do job.
        //
        // robot → usa o plugin "Robot Framework". Lê o output.xml e cria
        // um dashboard com pass/fail por suite/test, gráfico de tendência
        // entre builds, e link para o log detalhado.
        // ---------------------------------------------------------------------
        stage('Publicar Relatórios') {
            steps {
                publishHTML(target: [
                    allowMissing         : true,
                    alwaysLinkToLastBuild: true,
                    keepAll              : true,
                    reportDir            : "${RELATORIO_DIR}",
                    reportFiles          : 'report.html',
                    reportName           : 'Relatorio ExtentReports'
                ])

                robot(
                    outputPath      : "${ROBOT_OUTPUT}",
                    outputFileName  : 'output.xml',
                    reportFileName  : 'report.html',
                    logFileName     : 'log.html',
                    passThreshold   : 80.0,
                    unstableThreshold: 60.0,
                    onlyCritical    : false
                )
            }
        }
    }

    // =========================================================================
    // POST
    // Ações executadas APÓS todos os stages, independente do resultado.
    // Equivale ao 'after_script' do GitLab CI ou ao bloco 'finally' do Java.
    //
    // always  → roda sempre (sucesso, falha, unstable, abortado)
    // success → só roda se o build passou
    // failure → só roda se o build falhou
    // unstable→ roda se testes falharam mas o pipeline continuou
    // =========================================================================
    post {
        always {
            // Publica os resultados JUnit/Surefire (XML gerado pelo Maven).
            // Esses XMLs alimentam o gráfico de tendência de testes do Jenkins
            // e permitem ver histórico de falhas por test case ao longo do tempo.
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: true
        }

        success {
            echo 'Pipeline concluido com sucesso. Todos os testes passaram.'
        }

        unstable {
            echo 'Pipeline instavel: um ou mais testes falharam. Verifique os relatorios.'
        }

        failure {
            echo 'Pipeline falhou em um stage critico (nao foi teste). Verifique os logs.'
        }

        cleanup {
            // Limpa arquivos temporários do workspace para não acumular lixo
            // entre builds. Mantém os artefatos arquivados, mas remove o resto.
            cleanWs(patterns: [
                [pattern: 'target/', type: 'INCLUDE'],
                [pattern: "${ROBOT_OUTPUT}/", type: 'INCLUDE']
            ])
        }
    }
}
