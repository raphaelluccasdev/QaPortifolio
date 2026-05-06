package Steps;

import Pages.PaginaInicialPage;
import Utils.ReportUtils;
import Utils.seleniumUtils;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.util.Assert;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;

public class PaginaInicialSteps {
	PaginaInicialPage page = new PaginaInicialPage();

	@Dado("que eu acesse o site")
	public void queEuAcesseOSite() throws Throwable {
		page.acessaUrl();
		ReportUtils.logMensagem(Status.INFO, "Dado que eu acesse o site ", seleniumUtils.getScreenshotReport());
	}

    @E("que eu valide a logo")
	public void queEuValideALogo() throws Throwable {
		Assertions.assertTrue(page.validaExistenciaLogo());
		ReportUtils.logMensagem(Status.INFO, "E que eu valide a logo ", seleniumUtils.getScreenshotReport());
	}

	@E("que eu valide que estou na tela Home")
	public void queEuValideQueEstouNaTelaHome() throws Throwable {
		Assertions.assertTrue(page.validaExistenciaPaginaInicial());
		ReportUtils.logMensagem(Status.INFO, "E que eu valide que estou na tela Home", seleniumUtils.getScreenshotReport());
	}

	@Quando("eu clico no botão Cart")
	public void euClicoNoBotaoCart() throws Throwable {
		page.clicaBotaoCart();
		ReportUtils.logMensagem(Status.INFO, "Quando eu clico no botão Cart", seleniumUtils.getScreenshotReport());
	}

	@Então("eu valido que estou na tela Cart")
	public void euValidoQueEstouNaTelaCart() throws Throwable {
		Assertions.assertTrue(page.validaExistenciaCart());
		ReportUtils.logMensagem(Status.INFO, "Então eu valido que estou na tela de Cart");
	}

	@Então("eu valido que estou na tela de Login")
	public void euValidoQueEstouNaTelaDeLogin() throws Throwable {
		Assertions.assertTrue(page.validaExistenciaLogin());
		ReportUtils.logMensagem(Status.INFO, "Então eu valido que estou na tela de Login");
	}

	@Quando("eu clico no botão Login")
	public void euClicoNoBotaoLogin() throws Throwable {
		page.clicaBotaoLogin();
		ReportUtils.logMensagem(Status.INFO, "Quando eu clico no botão Login", seleniumUtils.getScreenshotReport());
	}

	@Quando("eu clico no botao Test")
	public void euClicoNoBotaoTest() throws  Throwable {
		page.clicaBotaoTest();
		ReportUtils.logMensagem(Status.INFO, "Quando eu clico no botao Test", seleniumUtils.getScreenshotReport());
	}

	@Quando("eu valido que estou na tela de Test")
	public void euValidoQueEstouNaTelaDeTest() throws  Throwable {
		Assertions.assertTrue(page.validaExistenciaTest());
		ReportUtils.logMensagem(Status.INFO, "Então eu valido que estou na tela de Test");
	}
}
