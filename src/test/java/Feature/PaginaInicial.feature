#language:pt
@TesteAutomatizadova
@PaginaInicialBotoesHeader
Funcionalidade: Acessar o sistema e valida a os botões do Header

  Contexto: Entra na página principal
    Dado que eu acesse o site
    E que eu valide a logo
    E que eu valide que estou na tela Home

  @PaginaInicialBotaoProducts
  Cenário: Validar botão Cart
    Quando eu clico no botão Cart
    Então eu valido que estou na tela Cart

  @PaginaBotaoLogin
  Cenário: Validar botão Login
    Quando eu clico no botão Login
    Então eu valido que estou na tela de Login

  @PaginaBotaoTest
  Cenário: Validar botão Test
    Quando eu clico no botao Test
    Então eu valido que estou na tela de Test

#  @PaginaInicialOverlay
#  Cenário: Validar overlay ao passar o mouse sobre o produto
#    Quando eu passo o mouse sobre o primeiro produto
#    Então eu valido que o overlay do produto está visível
