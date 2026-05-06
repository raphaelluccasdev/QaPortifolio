package Utils;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.util.Assert;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import static Utils.seleniumUtils.*;


public class JavaUtils {

    private static final Logger log = LoggerFactory.getLogger(JavaUtils.class);

    public static String lerArquivoProperty(String nomeArquivoProperty, String chaveProperty) {
        Properties prop = new Properties();
        InputStream input = null;
        String path;
        if (usingJarFile()) {
            path = "";
        } else {
            path = "resources/";
        }
        String property = "";
        try {
            input = new FileInputStream(path + nomeArquivoProperty);
            prop.load(input);
            property = prop.getProperty(chaveProperty);
        } catch (IOException ex) {
            ReportUtils.logMensagem(Status.FAIL, "Arquivo de properties não encontrado. " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    ReportUtils.logMensagem(Status.FAIL, "Arquivo de properties não encontrado. " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return property;
    }

    public static Boolean usingJarFile() {
        boolean isjar;
        String runningJarName = new JavaUtils().getRunningJarName();
        if (runningJarName != null) {
            isjar = true;
        } else {
            isjar = false;
        }
        return isjar;
    }

    public String getRunningJarName() {
        String className = this.getClass().getName().replace('.', '/');
        String classJar = this.getClass().getResource("/" + className + ".class").toString();
        if (classJar.startsWith("jar:")) {
            String vals[] = classJar.split("/");
            for (String val : vals) {
                if (val.contains("!")) {
                    return val.substring(0, val.length() - 1);
                }
            }
        }
        return null;
    }

    public void uploadArquivo(String caminho) throws AWTException {
        try {
            String caminho_completo = System.getProperty("user.dir") + caminho;
            Robot robot = new Robot();
            robot.setAutoDelay(2000);
            // StringSelection selection = new StringSelection((caminho+nome_arquivo));
            StringSelection selection = new StringSelection(caminho_completo);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            robot.setAutoDelay(1000);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception e) {
            ReportUtils.logMensagem(Status.FAIL, "" + e.getMessage());
        }
    }

    public String somenteDigitos(String palavra) {
        String digitos = "";
        try {
            char[] letras = palavra.toCharArray();
            for (char letra : letras) {
                if (Character.isDigit(letra)) {
                    digitos += letra;
                }
            }
        } catch (Exception e) {
            ReportUtils.logMensagem(Status.FAIL, "" + e.getMessage());
        }
        return digitos;
    }

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getAmanha() {
        Date currentDate = new Date();
        long currentTimeInMillis = currentDate.getTime() + (24 * 60 * 60 * 1000);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(currentTimeInMillis);
    }

    public static String formataData(String data) throws ParseException {
        data = data.substring(0, 10);
        String dataArray[] = data.split(Pattern.quote("-"));

        return dataArray[2] + "/" + dataArray[1] + "/" + dataArray[0];
    }

    public String formataDataHora(String dataHora) {
        String data = dataHora.substring(0, 10);
        String dataArray[] = data.split(Pattern.quote("-"));
        data = dataArray[2] + "/" + dataArray[1] + "/" + dataArray[0];

        String hora = dataHora.substring(11, 16);

        String dataHoraFormatadas = data + " " + hora;

        return dataHoraFormatadas;
    }

    public static void tiraPrint() {
        String nomeCenario;
        try {
            nomeCenario = pegNomeDoCenarioDoCSV();
            getScreenshot(nomeCenario);
        } catch (IOException e) {
            ReportUtils.logMensagem(Status.FAIL, "" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void getScreenshot(String nomeCenario) {
        String dir = System.getProperty("user.dir");
        driver.getCurrentUrl();

        String nomePrint = getDateTime();
        nomePrint = nomePrint.replace("/", "-");
        nomePrint = nomePrint.replace(" ", "_");
        nomePrint = nomePrint.replace(":", "_");
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            copyFileUsingStream(scrFile, new File(dir + "\\screenshots\\" + nomeCenario + "\\" + nomePrint + ".jpg"));
        } catch (Exception e) {
            ReportUtils.logMensagem(Status.FAIL, "Erro ao salvar o Screenshot - " + e);
        }
    }

    static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (Exception e) {
            ReportUtils.logMensagem(Status.FAIL, "" + e.getMessage());
        } finally {
            is.close();
            os.close();
        }
    }

    public String formataStringMoedaParaDecimal(String str) {
        // Dado que a string seja no formato de moeda "R$ 9.999,99"
        String strFormatoMoedaConvertidoDecimal = "";
        if (str != null) {
            strFormatoMoedaConvertidoDecimal = str.substring(3).replace(".", "");
            strFormatoMoedaConvertidoDecimal = strFormatoMoedaConvertidoDecimal.replace(",", ".");
            strFormatoMoedaConvertidoDecimal = strFormatoMoedaConvertidoDecimal.replace("00", "0").trim();
        }
        return strFormatoMoedaConvertidoDecimal;
    }

    public String formataStringDecimalParaMoeda(String valor) {
        valor = "R$  " + valor;
        if (valor.length() <= 10) {
            valor = valor.replace(".0", ",00");
        }
        StringBuilder stringBuilder = new StringBuilder(valor);
        stringBuilder.insert(valor.length() - 6, '.');

        return stringBuilder.toString();
    }

    public static void preencherCamposDinamicamente(List<String> valores, List<WebElement> campos) {
        try {

            for (int i = 0; i < campos.size(); i++) {

                campos.get(i).clear();

                campos.get(i).sendKeys(valores.get(i));
                sleep(1000);
            }
        } catch (Exception e) {
            ReportUtils.logMensagem(Status.FAIL, "Erro ao preencher campos: " + e.getMessage());
        }
    }

    private Properties carregarPropriedades() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Não foi possível encontrar o arquivo config.properties");
                return null;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

    public boolean baixarOArquivoPdf(String urlDoArquivo, String nomeArquivo) throws InterruptedException {

        Properties props = carregarPropriedades();
        String browser = props.getProperty("driver.padrao", "CHROME").toUpperCase().trim();

        String projectRootPath = System.getProperty("user.dir");
        File downloadDir = new File(projectRootPath + File.separator + "downloads");

        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        File arquivoFinal = new File(downloadDir.getAbsolutePath() + File.separator + nomeArquivo);

        if (arquivoFinal.exists()) {
            arquivoFinal.delete();
        }

        WebDriver driver;

        switch (browser) {
            case "CHROME": {
                log.info("Iniciando download com o CHROME.");
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("download.default_directory", downloadDir.getAbsolutePath());
                prefs.put("plugins.always_open_pdf_externally", true);
                prefs.put("download.prompt_for_download", false);
                prefs.put("safebrowsing.enabled", false);
                prefs.put("safebrowsing.disable_download_protection", true);
                prefs.put("profile.default_content_settings.popups", 0);

                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setExperimentalOption("prefs", prefs);
                chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--safebrowsing-disable-download-protection");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--headless=new");

                driver = new ChromeDriver(chromeOptions);
                break;
            }

            case "FIREFOX": {
                log.info("Iniciando download com o FIREFOX.");
                FirefoxOptions firefoxOptions = new FirefoxOptions();

                firefoxOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

                firefoxOptions.addPreference("browser.download.folderList", 2);
                firefoxOptions.addPreference("browser.download.dir", downloadDir.getAbsolutePath());
                firefoxOptions.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream,application/pdf,application/zip,text/csv");
                firefoxOptions.addPreference("pdfjs.disabled", true);
                firefoxOptions.addPreference("browser.download.manager.showWhenStarting", false);
                firefoxOptions.addPreference("browser.download.manager.alertOnEXEOpen", false);
                firefoxOptions.addPreference("browser.download.manager.closeWhenDone", true);
                firefoxOptions.addPreference("browser.download.manager.useWindow", false);
                firefoxOptions.addPreference("browser.safebrowsing.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.malware.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.downloads.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.downloads.remote.enabled", false);
                firefoxOptions.addPreference("browser.safebrowsing.downloads.remote.block_potentially_unwanted", false);
                firefoxOptions.addPreference("browser.safebrowsing.downloads.remote.block_uncommon", false);

                firefoxOptions.addArguments("--headless");

                driver = new FirefoxDriver(firefoxOptions);
                break;
            }


            default:
                throw new IllegalArgumentException("Navegador não suportado! Verifique a propriedade 'driver.padrao' em config.properties. Valor recebido: " + browser);
        }

        boolean downloadConcluido = false;
        try {


            log.info("Navegando via JavaScript para evitar travamento...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.location.href = arguments[0]", urlDoArquivo);

            long tempoLimite = System.currentTimeMillis() + 20000;
            log.info("Aguardando download do arquivo em: " + arquivoFinal.getAbsolutePath());

            while (System.currentTimeMillis() < tempoLimite) {
                boolean arquivoExiste = arquivoFinal.exists() && arquivoFinal.length() > 0;

                if (browser.equals("CHROME")) {
                    File arquivoTemporario = new File(downloadDir.getAbsolutePath() + File.separator + nomeArquivo + ".crdownload");
                    if (arquivoExiste && !arquivoTemporario.exists()) {
                        downloadConcluido = true;
                        break;
                    }
                } else {
                    if (arquivoExiste) {
                        downloadConcluido = true;
                        break;
                    }
                }
                Thread.sleep(1000);
            }

        } finally {
            log.info("Fechando o navegador.");
            if (driver != null) {
                driver.quit();
            }
        }

        return downloadConcluido;
    }
}
