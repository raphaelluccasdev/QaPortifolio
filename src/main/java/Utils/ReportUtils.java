package Utils;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.cucumber.java.Scenario;

public class ReportUtils extends seleniumUtils {

	public static ExtentSparkReporter htmlReporter;
	public static ExtentReports extentReport;
	public static ExtentTest extentTest;
	public static String diretorioReport;

	public static void criarReport(Scenario cenario) {
		if (extentReport == null) {
			String dir = System.getProperty("user.dir");
			String reportDir = dir + "/report";
			setDiretorioReport(reportDir);
			seleniumUtils.criarDiretorio(reportDir);
			seleniumUtils.criarDiretorio(reportDir + "/screenshots");

			htmlReporter = new ExtentSparkReporter(reportDir + "/report.html");
			htmlReporter.config().setDocumentTitle("Relatório de Testes - QA Portfolio");
			htmlReporter.config().setReportName("Automação Web");
			htmlReporter.config().setTheme(Theme.DARK);

			extentReport = new ExtentReports();
			extentReport.setSystemInfo("Ambiente", "QA");
			extentReport.setSystemInfo("Browser", "Chrome");
			extentReport.setSystemInfo("Framework", "Selenium + Cucumber");
			extentReport.attachReporter(htmlReporter);
		}
		extentTest = extentReport.createTest(cenario.getName());
	}

	public static void atualizaReport(Scenario cenario) {
		if (cenario.isFailed()) {
			extentTest.log(Status.FAIL, "Erro encontrado durante a execução.");
		} else {
			extentTest.log(Status.PASS, "Cenário executado com sucesso.");
		}
		extentReport.flush();
	}

	public static ExtentTest getExtentTest() {
		return extentTest;
	}

	public static void setDiretorioReport(String diretorio) {
		diretorioReport = diretorio;
	}

	public static String getDiretorioReport() {
		return diretorioReport;
	}

	public static void logMensagem(Status status, String mensagem, String imagem) {
		try {
			System.out.println(mensagem);
			extentTest.log(status, mensagem, MediaEntityBuilder.createScreenCaptureFromPath(imagem).build());
			extentReport.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void logMensagem(Status status, String mensagem) {
		System.out.println(mensagem);
		extentTest.log(status, mensagem);
		extentReport.flush();
	}

	public static void limpaDiretorios() {
		File report = new File(System.getProperty("user.dir") + "/report");
		deletarArquivo(report);
	}
}
