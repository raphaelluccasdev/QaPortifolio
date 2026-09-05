package Setups;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.aventstack.extentreports.Status;

import Utils.ReportUtils;
import Utils.seleniumUtils;

import java.net.MalformedURLException;
import java.net.URI;

public class TestRule {

	protected static WebDriver driver;
	public static String nomeCenario;
	
	@Before
	public void beforeCenario(Scenario cenario) throws MalformedURLException {
		ReportUtils.criarReport(cenario);
		ReportUtils.logMensagem(Status.INFO, "Iniciando Teste.");
		try {
			ChromeOptions options = new ChromeOptions();
			String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");

			if (remoteUrl != null && !remoteUrl.isBlank()) {
				driver = new RemoteWebDriver(URI.create(remoteUrl).toURL(), options);
			} else {
				driver = new ChromeDriver(options);
			}

			driver.manage().window().maximize();
			nomeCenario = cenario.getName();
			ReportUtils.logMensagem(Status.INFO, "Executando Cenário: " + nomeCenario);
		} catch (Exception e) {
			ReportUtils.logMensagem(Status.FAIL, "Erro ao iniciar o ChromeDriver: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static WebDriver getDriver() {
		return driver;
	}

	public static String getNomeCenario() {
		return nomeCenario;
	}

	@After
	public void afterCenario(Scenario cenario) {
		ReportUtils.logMensagem(Status.INFO, "Finalizando Instâncias", seleniumUtils.getScreenshotReport());
		ReportUtils.atualizaReport(cenario);
		seleniumUtils.sleep(1000);
		if (driver != null) {
			driver.quit();
		}
	}
}
