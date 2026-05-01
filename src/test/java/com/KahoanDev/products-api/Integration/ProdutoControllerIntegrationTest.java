package com.KahoanDev.poc_crud_api.Integration;

import com.KahoanDev.poc_crud_api.Controllers.dto.ProdutoDTO;
import com.KahoanDev.poc_crud_api.Model.enums.TipoProduto;
import com.KahoanDev.poc_crud_api.Repository.ProdutoRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProdutoControllerIntegrationTest {

    // =========================================================================
    // TESTCONTAINERS
    // =========================================================================

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("integration_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configurarDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",         postgres::getJdbcUrl);
        registry.add("spring.datasource.username",    postgres::getUsername);
        registry.add("spring.datasource.password",    postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    // =========================================================================
    // SETUP
    // =========================================================================

    @LocalServerPort
    int port;

    @Autowired
    ProdutoRepository repository;

    private RequestSpecification specification;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/produto")
                .setPort(port)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        repository.deleteAll();
    }


    // =========================================================================
    // HELPER
    // =========================================================================

    private Long extrairIdDoLocation(String location) {
        String[] partes = location.split("/");
        return Long.parseLong(partes[partes.length - 1]);
    }

    private Long criarProdutoERetornarId(ProdutoDTO dto) {
        String location = given().spec(specification)
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                    .post()
                .then()
                    .statusCode(201)
                .extract()
                    .header("Location");

        return extrairIdDoLocation(location);
    }

    // =========================================================================
    // POST
    // =========================================================================

    @Nested
    @DisplayName("POST /produto")
    class Salvar {

        @Test
        @DisplayName("deve retornar 201 com header Location ao criar produto válido")
        void integrationTest_When_CreateOneProduto_ShouldReturn_201WithLocationHeader() {
            // Given / Arrange
            var dto = new ProdutoDTO(null, "Notebook Dell", TipoProduto.ELETRONICO, 10L);

            // When / Act
            String location = given().spec(specification)
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .when()
                        .post()
                    .then()
                        .statusCode(201)
                    .extract()
                        .header("Location");

            // Then / Assert
            assertNotNull(location);
            assertTrue(location.contains("/produto/"));
            assertTrue(extrairIdDoLocation(location) > 0);
        }

        @Test
        @DisplayName("deve retornar 409 ao tentar cadastrar produto duplicado")
        void integrationTest_When_CreateDuplicatedProduto_ShouldReturn_409Conflict() {
            // Given / Arrange
            var dto = new ProdutoDTO(null, "Notebook Dell", TipoProduto.ELETRONICO, 10L);
            criarProdutoERetornarId(dto);

            // When / Act + Then / Assert
            given().spec(specification)
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .when()
                        .post()
                    .then()
                        .statusCode(409);
        }

        @Test
        @DisplayName("deve retornar 400 ao criar produto com body inválido")
        void integrationTest_When_CreateProdutoWithInvalidBody_ShouldReturn_400BadRequest() {
            // Given / Arrange
            var bodyInvalido = "{}";

            // When / Act + Then / Assert
            given().spec(specification)
                    .contentType(ContentType.JSON)
                    .body(bodyInvalido)
                    .when()
                        .post()
                    .then()
                        .statusCode(400);
        }

        @Test
        @DisplayName("deve persistir o produto no banco ao criar com sucesso")
        void integrationTest_When_CreateOneProduto_ShouldPersist_InDatabase() {
            // Given / Arrange
            var dto = new ProdutoDTO(null, "SSD 1TB", TipoProduto.ELETRONICO, 20L);

            // When / Act
            Long id = criarProdutoERetornarId(dto);

            // Then / Assert
            var salvo = repository.findById(id);
            assertTrue(salvo.isPresent());
            assertNotNull(salvo.get().getDescricao());
            assertEquals("SSD 1TB", salvo.get().getDescricao());
            assertEquals(20L, salvo.get().getQuantidade());
        }
    }

    // =========================================================================
    // GET
    // =========================================================================

    @Nested
    @DisplayName("GET /produto")
    class Pesquisar {

        @Test
        @DisplayName("deve retornar lista vazia quando não há produtos cadastrados")
        void integrationTest_When_FindAllWithEmptyDatabase_ShouldReturn_EmptyList() throws IOException {
            // Given / Arrange — banco limpo pelo @BeforeEach

            // When / Act
            var content = given().spec(specification)
                    .when()
                        .get()
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            // Then / Assert
            List<ProdutoDTO> lista = Arrays.asList(mapper.readValue(content, ProdutoDTO[].class));
            assertTrue(lista.isEmpty());
        }

        @Test
        @DisplayName("deve retornar todos os produtos cadastrados ao listar")
        void integrationTest_When_FindAllWithTwoProdutos_ShouldReturn_AProdutoList() throws IOException {
            // Given / Arrange
            criarProdutoERetornarId(new ProdutoDTO(null, "Produto A", TipoProduto.ELETRONICO, 1L));
            criarProdutoERetornarId(new ProdutoDTO(null, "Produto B", TipoProduto.ELETRONICO, 2L));

            // When / Act
            var content = given().spec(specification)
                    .when()
                        .get()
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            // Then / Assert
            List<ProdutoDTO> lista = Arrays.asList(mapper.readValue(content, ProdutoDTO[].class));
            assertNotNull(lista);
            assertEquals(2, lista.size());
            assertTrue(lista.stream().anyMatch(p -> p.descricao().equals("Produto A")));
            assertTrue(lista.stream().anyMatch(p -> p.descricao().equals("Produto B")));
        }

        @Test
        @DisplayName("deve retornar o produto correto ao buscar por id")
        void integrationTest_When_FindById_ShouldReturn_AProdutoObject() throws IOException {
            // Given / Arrange
            Long id = criarProdutoERetornarId(
                    new ProdutoDTO(null, "Headset Sony", TipoProduto.ELETRONICO, 7L));

            // When / Act
            var content = given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            // Then / Assert
            ProdutoDTO encontrado = mapper.readValue(content, ProdutoDTO.class);
            assertNotNull(encontrado);
            assertNotNull(encontrado.id());
            assertTrue(encontrado.id() > 0);
            assertEquals("Headset Sony", encontrado.descricao());
            assertEquals(TipoProduto.ELETRONICO, encontrado.tipo());
            assertEquals(7L, encontrado.quantidade());
        }

        @Test
        @DisplayName("deve retornar 404 ao buscar por id inexistente")
        void integrationTest_When_FindByNonExistingId_ShouldReturn_404NotFound() {
            // Given / Arrange
            Long idInexistente = 99999L;

            // When / Act + Then / Assert
            given().spec(specification)
                    .pathParam("id", idInexistente)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(404);
        }
    }

    // =========================================================================
    // PUT
    // =========================================================================

    @Nested
    @DisplayName("PUT /produto/{id}")
    class Atualizar {

        @Test
        @DisplayName("deve retornar 204 e persistir as mudanças ao atualizar produto existente")
        void integrationTest_When_UpdateOneProduto_ShouldReturn_AUpdatedProdutoObject() throws IOException {
            // Given / Arrange
            Long id = criarProdutoERetornarId(
                    new ProdutoDTO(null, "Webcam Logitech", TipoProduto.ELETRONICO, 4L));

            var dtoAtualizado = new ProdutoDTO(null, "Webcam Logitech 4K", TipoProduto.ELETRONICO, 10L);

            // When / Act
            given().spec(specification)
                    .contentType(ContentType.JSON)
                    .body(dtoAtualizado)
                    .pathParam("id", id)
                    .when()
                        .put("/{id}")
                    .then()
                        .statusCode(204);

            // Then / Assert — confirma via GET
            var content = given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            ProdutoDTO atualizado = mapper.readValue(content, ProdutoDTO.class);
            assertNotNull(atualizado);
            assertNotNull(atualizado.id());
            assertEquals("Webcam Logitech 4K", atualizado.descricao());
            assertEquals(10L, atualizado.quantidade());
        }

        @Test
        @DisplayName("deve retornar 404 ao tentar atualizar produto com id inexistente")
        void integrationTest_When_UpdateByNonExistingId_ShouldReturn_404NotFound() {
            // Given / Arrange
            var dto = new ProdutoDTO(null, "Qualquer", TipoProduto.ELETRONICO, 1L);

            // When / Act + Then / Assert
            given().spec(specification)
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .pathParam("id", 99999L)
                    .when()
                        .put("/{id}")
                    .then()
                        .statusCode(404);
        }
    }

    // =========================================================================
    // DELETE
    // =========================================================================

    @Nested
    @DisplayName("DELETE /produto/{id}")
    class Deletar {

        @Test
        @DisplayName("deve retornar 204 e remover o produto ao deletar por id existente")
        void integrationTest_When_DeleteOneProduto_ShouldReturn_NoContent() {
            // Given / Arrange
            Long id = criarProdutoERetornarId(
                    new ProdutoDTO(null, "Produto X", TipoProduto.ELETRONICO, 5L));

            // When / Act
            given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .delete("/{id}")
                    .then()
                        .statusCode(204);

            // Then / Assert — recurso não existe mais
            given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(404);

            assertFalse(repository.existsById(id));
        }

        @Test
        @DisplayName("deve manter apenas o produto restante após deletar um de dois")
        void integrationTest_When_DeleteOneOfTwoProdutos_ShouldKeep_OnlyTheOther() throws IOException {
            // Given / Arrange
            Long id1 = criarProdutoERetornarId(new ProdutoDTO(null, "Produto X", TipoProduto.ELETRONICO, 5L));
            Long id2 = criarProdutoERetornarId(new ProdutoDTO(null, "Produto Y", TipoProduto.ELETRONICO, 3L));

            // When / Act
            given().spec(specification)
                    .pathParam("id", id1)
                    .when()
                        .delete("/{id}")
                    .then()
                        .statusCode(204);

            // Then / Assert
            var content = given().spec(specification)
                    .when()
                        .get()
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            List<ProdutoDTO> lista = Arrays.asList(mapper.readValue(content, ProdutoDTO[].class));
            assertNotNull(lista);
            assertEquals(1, lista.size());
            assertEquals("Produto Y", lista.getFirst().descricao());
            assertFalse(repository.existsById(id1));
            assertTrue(repository.existsById(id2));
        }

        @Test
        @DisplayName("deve retornar 404 ao tentar deletar produto com id inexistente")
        void integrationTest_When_DeleteByNonExistingId_ShouldReturn_404NotFound() {
            // Given / Arrange
            Long idInexistente = 99999L;

            // When / Act + Then / Assert
            given().spec(specification)
                    .pathParam("id", idInexistente)
                    .when()
                        .delete("/{id}")
                    .then()
                        .statusCode(404);
        }
    }

    // =========================================================================
    // FLUXO COMPLETO
    // =========================================================================

    @Nested
    @DisplayName("Fluxo completo CRUD")
    class FluxoCompleto {

        @Test
        @DisplayName("deve executar o ciclo completo de CRUD com sucesso")
        void integrationTest_When_FullCrudIsExecuted_ShouldSucceed_OnEveryStep() throws IOException {
            // Given / Arrange
            var dto = new ProdutoDTO(null, "Teclado Mecânico", TipoProduto.ELETRONICO, 5L);

            // When / Act — CREATE
            Long id = criarProdutoERetornarId(dto);

            // Then / Assert
            assertNotNull(id);
            assertTrue(id > 0);

            // When / Act — READ por id
            var contentGet = given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            // Then / Assert
            ProdutoDTO criado = mapper.readValue(contentGet, ProdutoDTO.class);
            assertNotNull(criado);
            assertNotNull(criado.id());
            assertEquals("Teclado Mecânico", criado.descricao());
            assertEquals(TipoProduto.ELETRONICO, criado.tipo());
            assertEquals(5L, criado.quantidade());

            // When / Act — UPDATE
            given().spec(specification)
                    .contentType(ContentType.JSON)
                    .body(new ProdutoDTO(null, "Teclado Mecânico RGB", TipoProduto.ELETRONICO, 8L))
                    .pathParam("id", id)
                    .when()
                        .put("/{id}")
                    .then()
                        .statusCode(204);

            // Then / Assert
            var contentAtualizado = given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(200)
                    .extract()
                        .body()
                            .asString();

            ProdutoDTO atualizado = mapper.readValue(contentAtualizado, ProdutoDTO.class);
            assertNotNull(atualizado);
            assertEquals("Teclado Mecânico RGB", atualizado.descricao());
            assertEquals(8L, atualizado.quantidade());

            // When / Act — DELETE
            given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .delete("/{id}")
                    .then()
                        .statusCode(204);

            // Then / Assert
            given().spec(specification)
                    .pathParam("id", id)
                    .when()
                        .get("/{id}")
                    .then()
                        .statusCode(404);

            assertFalse(repository.existsById(id));
        }
    }

}
