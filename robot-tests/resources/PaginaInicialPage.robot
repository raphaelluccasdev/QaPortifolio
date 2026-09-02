*** Settings ***
Documentation    Page Object da Página Inicial.
...              Contém os locators e keywords de interação com a página.
Library          SeleniumLibrary
Resource         config.robot

*** Variables ***
${LOGO}                    css:.col-sm-4 .logo
${SIDEBAR_HOME}            css:.col-sm-3 .left-sidebar
${TEXTO_SHOPPING_CART}     css:.breadcrumb .active
${BOTAO_LOGIN}             css:.col-sm-4 .login-form .btn
${TEXTO_TEST}              css:.row .col-sm-9 .text-center

${BOTAO_CART}              css:.shop-menu a[href="/view_cart"]
${BOTAO_LOGIN_HEADER}      css:.shop-menu a[href="/login"]
${BOTAO_TEST}              css:.shop-menu a[href="/test_cases"]
${IMAGEM_PRE_DROPDOWN}     css:.productinfo
${OVERLAY}                 css:.product-overlay .overlay-content

*** Keywords ***

Abrir Navegador E Acessar Site
    ${options}=    Evaluate    selenium.webdriver.ChromeOptions()    selenium.webdriver
    Call Method    ${options}    add_argument    --no-sandbox
    Call Method    ${options}    add_argument    --disable-dev-shm-usage
    Call Method    ${options}    add_argument    --disable-popup-blocking
    Call Method    ${options}    add_argument    --disable-notifications
    Call Method    ${options}    set_capability    pageLoadStrategy    eager
    Open Browser    ${URL_SITE}    ${BROWSER}    options=${options}
    Maximize Browser Window

Fechar Navegador
    Close Browser

Acessar O Site
    Go To    ${URL_SITE}

Validar A Logo
    Wait Until Element Is Visible    ${LOGO}    ${TIMEOUT}
    Element Should Be Visible        ${LOGO}

Validar Que Estou Na Tela Home
    Wait Until Element Is Visible    ${SIDEBAR_HOME}    ${TIMEOUT}
    Element Should Be Visible        ${SIDEBAR_HOME}

Clicar No Botão Cart
    Wait Until Element Is Visible    ${BOTAO_CART}    ${TIMEOUT}
    Click Element    ${BOTAO_CART}

Clicar No Botão Login
    Wait Until Element Is Visible    ${BOTAO_LOGIN_HEADER}    ${TIMEOUT}
    Click Element    ${BOTAO_LOGIN_HEADER}

Clicar No Botão Test
    Wait Until Element Is Visible    ${BOTAO_TEST}    ${TIMEOUT}
    Click Element    ${BOTAO_TEST}

Validar Que Estou Na Tela Cart
    Wait Until Element Is Visible    ${TEXTO_SHOPPING_CART}    ${TIMEOUT}
    Element Should Be Visible        ${TEXTO_SHOPPING_CART}

Validar Que Estou Na Tela De Login
    Wait Until Element Is Visible    ${BOTAO_LOGIN}    ${TIMEOUT}
    Element Should Be Visible        ${BOTAO_LOGIN}

Validar Que Estou Na Tela De Test
    Wait Until Element Is Visible    ${TEXTO_TEST}    ${TIMEOUT}
    Element Should Be Visible        ${TEXTO_TEST}

Voltar Para A Página Anterior
    Go Back
    Wait Until Element Is Visible    ${SIDEBAR_HOME}    ${TIMEOUT}

Passar Mouse Sobre Primeiro Produto
    ${produtos}=    Get WebElements    ${IMAGEM_PRE_DROPDOWN}
    Mouse Over    ${produtos}[0]

Validar Que O Overlay Está Visível
    ${overlays}=    Get WebElements    ${OVERLAY}
    Wait Until Element Is Visible    ${overlays}[0]    ${TIMEOUT}
    Element Should Be Visible        ${overlays}[0]
