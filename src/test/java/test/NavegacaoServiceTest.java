package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import Services.NavegacaoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do NavegacaoService usando Mockito.
 *
 * O QUE É UM MOCK?
 * Um mock é um objeto falso que substitui uma dependência real durante o teste.
 * Aqui, o WebDriver real abre um browser e precisa de Chrome instalado.
 * O WebDriver mock finge ser um driver — responde às chamadas que configuramos,
 * sem abrir nada.
 *
 * ESTRUTURA DE CADA TESTE (padrão AAA):
 *   Arrange — configura o mock e os dados de entrada
 *   Act     — chama o método que está sendo testado
 *   Assert  — verifica o resultado ou o comportamento
 */
@ExtendWith(MockitoExtension.class) // Habilita as anotações do Mockito nesta classe
class NavegacaoServiceTest {

    /**
     * @Mock cria automaticamente um objeto falso do tipo WebDriver.
     * Qualquer chamada a driver.getCurrentUrl(), driver.getTitle() etc.
     * retorna null/false por padrão, a menos que configuremos com when().
     */
    @Mock
    private WebDriver driver;

    /**
     * @Mock para o objeto Navigation, que é o que driver.navigate() retorna.
     * Precisamos mockár ele separadamente porque driver.navigate() também é
     * uma chamada ao mock — sem configurar, retornaria null.
     */
    @Mock
    private WebDriver.Navigation navigation;

    // O objeto que estamos testando de verdade.
    // Chamamos de "system under test" (SUT).
    private NavegacaoService navegacaoService;

    /**
     * @BeforeEach roda antes de cada teste.
     * Criamos o NavegacaoService passando o driver mockado — sem browser real.
     * Esse é o ponto central do padrão: injeção de dependência permite
     * passar um mock no lugar do objeto real.
     */
    @BeforeEach
    void setUp() {
        navegacaoService = new NavegacaoService(driver);
    }

    // =========================================================================
    // TESTES DE NAVEGAÇÃO
    // =========================================================================

    @Test
    @DisplayName("Deve chamar driver.navigate().to() com a URL correta")
    void deveNaveparParaUrlInformada() {
        // Arrange
        // Configuramos o mock: quando alguém chamar driver.navigate(),
        // ele retorna o objeto 'navigation' (também um mock).
        when(driver.navigate()).thenReturn(navigation);
        String urlEsperada = "https://automationexercise.com";

        // Act
        navegacaoService.navegarPara(urlEsperada);

        // Assert
        // verify() confirma que o método foi chamado com os parâmetros certos.
        // Se navegarPara() não tivesse chamado navigation.to(), o teste falharia.
        // Isso valida o COMPORTAMENTO, não só o retorno.
        verify(navigation).to(urlEsperada);
    }

    @Test
    @DisplayName("Deve retornar a URL atual do driver")
    void deveRetornarUrlAtual() {
        // Arrange
        // when(mock.metodo()).thenReturn(valor) configura o que o mock
        // retorna quando aquele método é chamado.
        String urlFalsa = "https://automationexercise.com/products";
        when(driver.getCurrentUrl()).thenReturn(urlFalsa);

        // Act
        String urlRetornada = navegacaoService.obterUrlAtual();

        // Assert
        // assertEquals verifica que o serviço retornou exatamente o que
        // o driver retornou — sem transformar ou perder o valor.
        assertEquals(urlFalsa, urlRetornada);
    }

    @Test
    @DisplayName("Deve retornar o título da página atual")
    void deveRetornarTituloDaPagina() {
        // Arrange
        when(driver.getTitle()).thenReturn("Automation Exercise");

        // Act
        String titulo = navegacaoService.obterTituloPagina();

        // Assert
        assertEquals("Automation Exercise", titulo);
    }

    // =========================================================================
    // TESTES DE LÓGICA: urlContem()
    // Esses testes são especialmente importantes porque o método tem
    // lógica própria (o contains). Testamos os casos: verdadeiro, falso e
    // case-sensitive — cada comportamento possível do método.
    // =========================================================================

    @Test
    @DisplayName("urlContem() deve retornar true quando URL contém o fragmento")
    void urlContemDeveRetornarTrueQuandoFragmentoPresente() {
        // Arrange
        when(driver.getCurrentUrl()).thenReturn("https://automationexercise.com/cart");

        // Act
        boolean resultado = navegacaoService.urlContem("cart");

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("urlContem() deve retornar false quando URL não contém o fragmento")
    void urlContemDeveRetornarFalseQuandoFragmentoAusente() {
        // Arrange
        when(driver.getCurrentUrl()).thenReturn("https://automationexercise.com/products");

        // Act
        boolean resultado = navegacaoService.urlContem("cart");

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("urlContem() deve ser case-sensitive")
    void urlContemDeveSerCaseSensitive() {
        // Arrange
        // A URL tem "cart" em minúsculo. Buscamos "CART" em maiúsculo.
        // String.contains() é case-sensitive — esperamos false.
        // Esse teste documenta esse comportamento para quem ler o código no futuro.
        when(driver.getCurrentUrl()).thenReturn("https://automationexercise.com/cart");

        // Act
        boolean resultado = navegacaoService.urlContem("CART");

        // Assert
        assertFalse(resultado, "urlContem() deve ser case-sensitive");
    }

    // =========================================================================
    // TESTES DE LÓGICA: tituloPaginaIgual()
    // =========================================================================

    @Test
    @DisplayName("tituloPaginaIgual() deve retornar true para título exato")
    void tituloPaginaIgualDeveRetornarTrueParaTituloCorreto() {
        // Arrange
        when(driver.getTitle()).thenReturn("Automation Exercise");

        // Act + Assert
        // Podemos combinar Act e Assert quando a linha é simples o suficiente.
        assertTrue(navegacaoService.tituloPaginaIgual("Automation Exercise"));
    }

    @Test
    @DisplayName("tituloPaginaIgual() deve retornar false para título diferente")
    void tituloPaginaIgualDeveRetornarFalseParaTituloDiferente() {
        // Arrange
        when(driver.getTitle()).thenReturn("Automation Exercise");

        // Act + Assert
        assertFalse(navegacaoService.tituloPaginaIgual("Página Errada"));
    }

    // =========================================================================
    // TESTE DE INTERAÇÃO: verifica quantas vezes o mock foi chamado.
    // times(N) é útil quando você quer garantir que não há chamadas
    // desnecessárias ao driver (evita performance ruim em testes reais).
    // =========================================================================

    @Test
    @DisplayName("Deve consultar getCurrentUrl exatamente uma vez")
    void deveConsultarUrlApenasUmaVez() {
        // Arrange
        when(driver.getCurrentUrl()).thenReturn("https://automationexercise.com");

        // Act
        navegacaoService.urlContem("automationexercise");

        // Assert
        // verify(mock, times(N)) garante que o método foi chamado N vezes.
        // Se o código chamasse getCurrentUrl() duas vezes (ineficiência),
        // esse teste detectaria o problema.
        verify(driver, times(1)).getCurrentUrl();
    }
}
