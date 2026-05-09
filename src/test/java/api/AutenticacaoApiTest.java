package api;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de API para o endpoint de Autenticação.
 * Site testado: https://automationexercise.com/api_list
 *
 * -------------------------------------------------------------------------
 * POR QUE TESTAR AUTENTICAÇÃO NA CAMADA DE API?
 * A autenticação é uma das áreas mais críticas de qualquer sistema.
 * Testar via API (sem browser) garante que:
 *   1. A validação de credenciais acontece no back-end, não só no front-end
 *   2. Mensagens de erro específicas são retornadas corretamente
 *   3. Métodos HTTP não permitidos são rejeitados
 *
 * Esses testes protegem contra regressões silenciosas — quando o back-end
 * muda o comportamento sem que o front-end perceba imediatamente.
 *
 * -------------------------------------------------------------------------
 * CENÁRIOS COBERTOS:
 *
 * 1. Credenciais inválidas → responseCode 404 ("User not found!")
 *    Valida que a API rejeita usuários inexistentes corretamente.
 *
 * 2. Parâmetro ausente → responseCode 400 (Bad Request)
 *    Valida que a API rejeita requisições incompletas com mensagem clara.
 *
 * 3. Método DELETE não suportado → responseCode 405
 *    Valida que a API rejeita métodos HTTP incorretos.
 *
 * 4. Ambos os campos ausentes → responseCode 400
 *    Testa o caso extremo (edge case) de request completamente vazia.
 *
 * 5. Apenas senha ausente → responseCode 400
 *    Testa outra variação do campo faltante (email presente, senha ausente).
 */
@DisplayName("API - Autenticação")
class AutenticacaoApiTest {

    @BeforeAll
    static void configurar() {
        RestAssured.baseURI = "https://automationexercise.com";
        RestAssured.registerParser("text/html", Parser.JSON);
    }

    // =========================================================================
    // TESTES DE CREDENCIAIS INVÁLIDAS
    // Essas são as respostas esperadas para requisições com dados errados.
    // O padrão desta API: HTTP status sempre 200, status real no responseCode.
    // =========================================================================

    @Test
    @DisplayName("POST /api/verifyLogin - credenciais inválidas devem retornar responseCode 404")
    void credenciaisInvalidasDevemRetornar404() {
        /*
         * Enviamos email e senha que não existem no sistema.
         * A API retorna HTTP 200 mas com responseCode 404 no body.
         *
         * Por que 404? "User not found" — o recurso (usuário) não existe.
         * É uma convenção desta API específica usar códigos HTTP semânticos
         * dentro do JSON mesmo que o HTTP status seja sempre 200.
         */
        given()
            .formParam("email",    "usuario_que_nao_existe@teste.com")
            .formParam("password", "senha_errada_123")
        .when()
            .post("/api/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(404))
            .body("message",      equalTo("User not found!"));
    }

    @Test
    @DisplayName("POST /api/verifyLogin - sem parâmetro email deve retornar responseCode 400")
    void semEmailDeveRetornar400() {
        /*
         * Enviamos só a senha, sem o email.
         * A API retorna 400 (Bad Request) com mensagem explicando o que faltou.
         *
         * containsString() em vez de equalTo() porque a mensagem pode mudar
         * levemente (ex: mais campos listados), mas a palavra-chave "email"
         * deve sempre estar presente.
         *
         * Isso é uma prática de teste mais resiliente: validar a essência
         * da mensagem, não o texto exato.
         */
        given()
            .formParam("password", "qualquer_senha")
        .when()
            .post("/api/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message",      containsString("email"));
    }

    @Test
    @DisplayName("POST /api/verifyLogin - sem parâmetro password deve retornar responseCode 400")
    void semPasswordDeveRetornar400() {
        /*
         * Enviamos só o email, sem a senha.
         * Testa a outra variação do campo faltante.
         *
         * É importante testar cada campo individualmente — às vezes a API
         * valida apenas o primeiro parâmetro e ignora a ausência do segundo.
         * Esse teste detectaria esse bug.
         */
        given()
            .formParam("email", "qualquer@email.com")
        .when()
            .post("/api/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400))
            .body("message",      containsString("password"));
    }

    @Test
    @DisplayName("POST /api/verifyLogin - sem nenhum parâmetro deve retornar responseCode 400")
    void semParametrosDeveRetornar400() {
        /*
         * Caso extremo (edge case): requisição completamente vazia.
         * Um POST sem body é tecnicamente válido — é responsabilidade
         * da API validar que os campos obrigatórios estão presentes.
         *
         * Esse teste garante que a API não quebra (500 Internal Server Error)
         * quando recebe uma requisição vazia — ela deve responder graciosamente.
         */
        given()
        .when()
            .post("/api/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(400));
    }

    // =========================================================================
    // TESTE DE MÉTODO HTTP NÃO SUPORTADO
    // DELETE não faz sentido semânticamente para "verificar login".
    // A API deve rejeitar explicitamente com 405 (Method Not Allowed).
    // =========================================================================

    @Test
    @DisplayName("DELETE /api/verifyLogin - método não suportado deve retornar responseCode 405")
    void deleteNaoSuportadoDeveRetornar405() {
        /*
         * DELETE é um método de exclusão de recursos. Usá-lo em um endpoint
         * de verificação de login não tem sentido semântico.
         *
         * Testamos isso para garantir que a API não aceita métodos incorretos
         * acidentalmente — o que poderia abrir brechas de segurança se,
         * por exemplo, um DELETE retornasse dados de autenticação.
         *
         * Novamente: HTTP status 200, mas responseCode 405 no body.
         * "This request method is not supported." é a mensagem exata desta API.
         */
        given()
        .when()
            .delete("/api/verifyLogin")
        .then()
            .statusCode(200)
            .body("responseCode", equalTo(405))
            .body("message",      containsString("not supported"));
    }
}
