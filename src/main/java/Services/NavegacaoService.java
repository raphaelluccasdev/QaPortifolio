package Services;

import org.openqa.selenium.WebDriver;

/**
 * Serviço responsável por operações de navegação no browser.
 *
 * POR QUE ESSA CLASSE EXISTE?
 * A PaginaInicialPage busca o WebDriver diretamente de TestRule.getDriver(),
 * ou seja, ela cria a própria dependência. Isso torna impossível testar sem
 * abrir um browser de verdade.
 *
 * Aqui fazemos diferente: o WebDriver é RECEBIDO pelo construtor (injeção de
 * dependência). Assim, no teste unitário passamos um WebDriver falso (mock)
 * e verificamos o comportamento sem precisar de browser.
 *
 * Esse é o princípio de design que torna o código testável.
 */
public class NavegacaoService {

    // O WebDriver é uma DEPENDÊNCIA desta classe.
    // Ao declará-lo como campo final recebido pelo construtor,
    // quem cria o objeto decide qual driver usar — real ou mock.
    private final WebDriver driver;

    public NavegacaoService(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Navega para a URL informada.
     * No teste, verificamos se driver.navigate().to() foi chamado corretamente.
     */
    public void navegarPara(String url) {
        driver.navigate().to(url);
    }

    /**
     * Retorna a URL da página atual.
     * No teste, o mock retorna uma URL fictícia e verificamos que o método
     * a repassa corretamente.
     */
    public String obterUrlAtual() {
        return driver.getCurrentUrl();
    }

    /**
     * Retorna o título da aba do browser.
     */
    public String obterTituloPagina() {
        return driver.getTitle();
    }

    /**
     * Verifica se a URL atual contém o fragmento informado.
     * Exemplo: urlContem("cart") retorna true se a URL for ".../cart".
     *
     * Esse método tem LÓGICA PRÓPRIA (o contains) — por isso vale testar
     * em isolamento para garantir que a lógica está correta, independente
     * do browser.
     */
    public boolean urlContem(String fragmento) {
        return driver.getCurrentUrl().contains(fragmento);
    }

    /**
     * Verifica se o título da página é exatamente o esperado.
     * Útil para validar se a navegação chegou na página certa.
     */
    public boolean tituloPaginaIgual(String tituloEsperado) {
        return driver.getTitle().equals(tituloEsperado);
    }
}
