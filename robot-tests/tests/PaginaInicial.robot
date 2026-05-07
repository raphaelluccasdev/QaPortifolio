*** Settings ***
Documentation     Funcionalidade: Acessar o sistema e validar os botões do Header.
...               Espelha os cenários do arquivo PaginaInicial.feature (Cucumber).
Resource          ../resources/PaginaInicialPage.robot

Suite Setup       Abrir Navegador E Acessar Site
Suite Teardown    Fechar Navegador

Test Setup        Executar Contexto Inicial

*** Test Cases ***

Validar Botão Cart
    [Tags]    PaginaInicialBotoesHeader    PaginaInicialBotaoCart
    Clicar No Botão Cart
    Validar Que Estou Na Tela Cart
    Voltar Para A Página Anterior

Validar Botão Login
    [Tags]    PaginaInicialBotoesHeader    PaginaBotaoLogin
    Clicar No Botão Login
    Validar Que Estou Na Tela De Login
    Voltar Para A Página Anterior

Validar Botão Test
    [Tags]    PaginaInicialBotoesHeader    PaginaBotaoTest
    Clicar No Botão Test
    Validar Que Estou Na Tela De Test
    Voltar Para A Página Anterior

Validar Overlay Ao Passar Mouse Sobre Produto
    [Tags]    PaginaInicialOverlay
    Passar Mouse Sobre Primeiro Produto
    Validar Que O Overlay Está Visível

*** Keywords ***

Executar Contexto Inicial
    Acessar O Site
    Validar A Logo
    Validar Que Estou Na Tela Home
