package Elements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import Utils.seleniumUtils;

import java.util.List;

public class PaginaInicialElements extends seleniumUtils{

	@FindBy(css = ".col-sm-4 .logo")
	public WebElement LOGO;

	@FindBy	(css = ".col-sm-8 .shop-menu .navbar-nav li")
	public List<WebElement> BOTOES_HEADER;

	@FindBy(css = ".col-sm-3 .left-sidebar")
	public WebElement SIDEBAR_PAGINA_INICIAL;

	@FindBy(css = "#advertisement .container #sale_image")
	public WebElement LOGO_SALE_PRODUCTS;

//	@FindBy(id = "continue-prompt-text")
//	public WebElement BOTAO_FECHAR_AD;

	@FindBy(css = ".breadcrumb .active")
	public WebElement TEXTO_SHOPPING_CART;

	@FindBy(css = ".col-sm-4 .login-form .btn")
	public WebElement BOTAO_LOGIN;

	@FindBy(css = ".row .col-sm-9 .text-center")
	public WebElement TEXTO_TEST;

}
