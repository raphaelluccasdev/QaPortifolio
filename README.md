# Automação Web - QA Portfolio

Projeto de automação de testes web desenvolvido para demonstrar habilidades em QA, cobrindo os principais frameworks e ferramentas exigidos pelo mercado.

## Tecnologias utilizadas

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Automação web | Selenium 4 |
| BDD | Cucumber 7 |
| Testes alternativos | Robot Framework 7 + SeleniumLibrary |
| Build | Maven |
| Relatórios | ExtentReports |

## Estrutura do projeto

```
automacao-web/
├── src/
│   ├── main/java/
│   │   ├── Elements/       # Locators (Page Object)
│   │   ├── Pages/          # Ações por página
│   │   ├── Steps/          # Step definitions do Cucumber
│   │   ├── Setups/         # Configuração de browser (Before/After)
│   │   └── Utils/          # Selenium utilities e relatórios
│   └── test/java/
│       ├── Feature/        # Arquivos .feature em português (BDD)
│       └── test/           # TestRunner
├── robot-tests/
│   ├── resources/          # Page Objects e configurações do Robot
│   └── tests/              # Suites de teste Robot Framework
└── resources/              # Arquivos .properties (URL, ambiente)
```

## Cenários automatizados

O projeto testa a página inicial do site [Automation Exercise](https://automationexercise.com/), cobrindo os botões do header:

- Validar botão **Cart** — navega e valida a tela de carrinho
- Validar botão **Login** — navega e valida a tela de login
- Validar botão **Test Cases** — navega e valida a tela de casos de teste

Os mesmos cenários estão implementados duas vezes: uma em **Cucumber + Java** e outra em **Robot Framework**, para demonstrar domínio das duas abordagens.

## Como executar

### Pré-requisitos

- Java 21+
- Maven 3.8+
- Google Chrome instalado
- Python 3.8+ (para o Robot Framework)

### Cucumber + Java

```bash
mvn test
```

### Robot Framework

```bash
# Instalar dependências Python
pip install -r robot-tests/requirements.txt

# Rodar os testes
robot robot-tests/tests/PaginaInicial.robot
```

O relatório será gerado em `log.html` e `report.html` na raiz do projeto.

## Padrões utilizados

- **Page Object Model** — separação entre locators, ações e steps
- **BDD com Gherkin em português** — cenários escritos em linguagem natural
- **Hooks com Cucumber** (`@Before` / `@After`) — setup e teardown do browser
- **ExtentReports** — geração de relatórios com evidências em HTML
