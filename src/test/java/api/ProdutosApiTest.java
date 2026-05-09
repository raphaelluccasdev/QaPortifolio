package api;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de API para o endpoint de Produtos.
 * Site testado: https://automationexercise.com/api_list
 *
 * -------------------------------------------------------------------------
 * O QUE É RESTASSURED?
 * RestAssured é uma biblioteca Java para testar APIs REST. Ela permite
 * fazer requisições HTTP (GET, POST, PUT, DELETE) e validar a resposta
 * — status code, headers e body — de forma fluente e legível.
 *
 * POR QUE TESTAR A API ALÉM DO E2E?
 * Testes de API ficam na camada de integração da pirâmide de testes:
 * são mais rápidos que E2E (sem browser), mais estáveis (não quebram
 * por mudança visual) e isolam problemas no back-end antes de chegar
 * à interface.
 *
 * -------------------------------------------------------------------------
 * ESTRUTURA GIVEN / WHEN / THEN DO RESTASSURED:
 *
 *   given()   → configuração da requisição (headers, params, body, auth)
 *   .when()   → qual método HTTP e qual endpoint
 *   .then()   → o que validar na resposta
 *
 * Essa estrutura é idêntica ao Gherkin do Cucumber — não é coincidência.
 * RestAssured foi projetado para ser lido como especificação de comportamento.
 *
 * -------------------------------------------------------------------------
 * OBSERVAÇÃO IMPORTANTE SOBRE ESTA API:
 * O HTTP status code é sempre 200, mesmo quando há erro.
 * O status real da operação vem no campo "responseCode" dentro do JSON.
 * Isso é uma decisão de design (ruim, mas comum em APIs legadas).
 * Por isso validamos tanto o statusCode() quanto o body("responseCode").
 */
@DisplayName("API - Produtos")
class ProdutosApiTest {

    /**
     * @BeforeAll roda uma única vez antes de todos os testes da classe.
     * Configuramos a URL base aqui para não repetir em cada teste.
     * RestAssured.baseURI é uma variável global da biblioteca.
     */
    @BeforeAll
    static void configurar() {
        RestAssured.baseURI = "https://automationexercise.com";
        /*
         * Esta API retorna Content-Type: text/html mesmo que o body seja JSON puro.
         * O defaultParser só funciona para Content-Types desconhecidos — text/html
         * é reconhecido como HTML, então o RestAssured tentaria usar XML path.
         * registerParser mapeia explicitamente text/html → Parser.JSON, forçando
         * o uso correto de JSON path nas asserções de .body().
         */
        RestAssured.registerParser("text/html", Parser.JSON);
    }

    // =========================================================================
    // TESTES DE GET — listagem de produtos
    // =========================================================================

    @Test
    @DisplayName("GET /api/productsList - deve retornar HTTP 200")
    void deveRetornarStatus200AoListarProdutos() {
        /*
         * Teste mais simples possível: só verifica que a API respondeu.
         * statusCode(200) valida o HTTP status code da resposta.
         * Se a API estiver fora do ar ou o endpoint mudar, este teste falha.
         */
        given()
        .when()
            .get("/api/productsList")
        .then()
            .statusCode(200);
    }

    @Test
    @DisplayName("GET /api/productsList - resposta deve conter lista de produtos não vazia")
    void respostaDeveConterListaDeProdutosNaoVazia() {
        /*
         * body("products", notNullValue()) → o campo "products" existe no JSON
         * body("products.size()", greaterThan(0)) → a lista tem pelo menos 1 item
         *
         * O primeiro argumento de body() é um JSON Path:
         *   "products"        → acessa o campo raiz "products"
         *   "products.size()" → conta os itens do array
         *   "products[0].name"→ acessa o nome do primeiro produto
         */
        given()
        .when()
            .get("/api/productsList")
        .then()
            .statusCode(200)
            .body("products", notNullValue())
            .body("products.size()", greaterThan(0));
    }

    @Test
    @DisplayName("GET /api/productsList - cada produto deve ter id, nome, preço e marca")
    void cadaProdutoDeveTerCamposObrigatorios() {
        /*
         * Valida a estrutura do objeto. Se o back-end mudar o nome de um campo
         * (ex: "name" virar "productName"), este teste detecta imediatamente.
         * Isso protege os testes E2E de quebrar por razões de contrato de API.
         */
        given()
        .when()
            .get("/api/productsList")
        .then()
            .statusCode(200)
            .body("products[0].id",    notNullValue())
            .body("products[0].name",  notNullValue())
            .body("products[0].price", notNullValue())
            .body("products[0].brand", notNullValue());
    }

    @Test
    @DisplayName("GET /api/productsList - responseCode no body deve ser 200")
    void responseCodeNoBodtDeveSerDuzentos() {
        /*
         * Esta API usa responseCode dentro do JSON além do HTTP status.
         * Validamos os dois para garantir que a API está operacional de verdade,
         * não só respondendo com 200 vazio.
         */
        given()
        .when()
            .get("/api/productsList")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200));
    }

    // =========================================================================
    // TESTE DE MÉTODO NÃO SUPORTADO
    // Documenta o comportamento esperado quando a API recebe um método errado.
    // Isso é importante para garantir que a API rejeita corretamente
    // requisições inválidas.
    // =========================================================================

    @Test
    @DisplayName("POST /api/productsList - método não suportado deve retornar responseCode 405")
    void postNaoSuportadoDeveRetornar405NaResposta() {
        /*
         * A API não aceita POST neste endpoint.
         * O HTTP status ainda é 200 (decisão desta API específica),
         * mas o responseCode no body é 405 e a mensagem explica o motivo.
         *
         * containsString() verifica se a string está contida na mensagem —
         * mais robusto que equalTo() porque não quebra se a mensagem mudar
         * levemente.
         */
        given()
        .when()
            .post("/api/productsList")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(405))
            .body("message", containsString("not supported"));
    }

    // =========================================================================
    // TESTE DE POST COM PARÂMETROS — busca de produtos
    // =========================================================================

    @Test
    @DisplayName("POST /api/searchProduct - busca por 'top' deve retornar produtos")
    void buscaPorTermoDeveRetornarProdutosRelacionados() {
        /*
         * formParam() envia parâmetros no corpo da requisição como form-urlencoded
         * (o formato padrão de formulários HTML).
         *
         * Diferença entre formParam e queryParam:
         *   formParam → vai no body: POST /api/searchProduct   body: search_product=top
         *   queryParam → vai na URL: GET /api/search?search_product=top
         */
        given()
            .formParam("search_product", "top")
        .when()
            .post("/api/searchProduct")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(200))
            .body("products",         notNullValue())
            .body("products.size()",  greaterThan(0));
    }

    @Test
    @DisplayName("POST /api/searchProduct - produtos encontrados devem ter o termo no nome")
    void produtosEncontradosDevemConterTermoDeBusca() {
        /*
         * hasItem() verifica se pelo menos um elemento da lista satisfaz a condição.
         * "products.name" retorna a lista de todos os nomes dos produtos.
         *
         * Buscamos por "Top" (com T maiúsculo) porque a API retorna os nomes
         * capitalizados (ex: "Blue Top", "Winter Top"). containsString() é
         * case-sensitive — usamos o valor exato que a API devolve.
         */
        given()
            .formParam("search_product", "top")
        .when()
            .post("/api/searchProduct")
        .then()
            .statusCode(200)
            .body("products.name", hasItem(containsString("Top")));
    }
}
