package Pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.PageFactory;

import Elements.PaginaInicialElements;
import Setups.TestRule;

import static Utils.JavaUtils.lerArquivoProperty;

@Slf4j
public class PaginaInicialPage extends PaginaInicialElements {

	public PaginaInicialPage() {
		driver = TestRule.getDriver();
		PageFactory.initElements(TestRule.getDriver(), this);
	}

	private String getUrl() {
		return lerArquivoProperty("ambientes.properties", "URL_SITE");
	}

	public void acessaUrl() {
		driver.navigate().to(getUrl());
	}

	public void clicaBotaoCart() {
		esperaElementos(BOTOES_HEADER, 2);
		BOTOES_HEADER.get(2).click();
	}

	public void clicaBotaoLogin() {
		esperaElementos(BOTOES_HEADER, 2);
		BOTOES_HEADER.get(3).click();
	}

	public void clicaBotaoTest() {
		esperaElementos(BOTOES_HEADER, 2);
		BOTOES_HEADER.get(4).click();

	}

	public boolean validaExistenciaPaginaInicial() throws Throwable {
		return verificaExistenciaDeElementoNaTela(SIDEBAR_PAGINA_INICIAL, 1);
	}

	public boolean validaExistenciaCart() throws Throwable {
		return verificaExistenciaDeElementoNaTela(TEXTO_SHOPPING_CART, 1);
	}

	public boolean validaExistenciaLogin() throws Throwable {
		return verificaExistenciaDeElementoNaTela(BOTAO_LOGIN, 1);
	}

	public boolean validaExistenciaTest() throws Throwable {
		return verificaExistenciaDeElementoNaTela(TEXTO_TEST, 1);
	}
}
