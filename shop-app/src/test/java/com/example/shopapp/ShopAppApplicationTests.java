package com.example.shopapp;

import com.example.shopuser.dto.UserDto;
import com.example.shopuser.repository.UserRepository;
import com.example.shopuser.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShopAppApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void jpaCreatesUsersTableInConfiguredH2Database() throws Exception {
        assertThat(userRepository).isNotNull();

        try (Connection connection = dataSource.getConnection();
             ResultSet tables = connection.getMetaData()
                     .getTables(null, null, "USERS", new String[]{"TABLE"})) {
            assertThat(connection.getMetaData().getURL()).isEqualTo("jdbc:h2:mem:shopdb");
            assertThat(tables.next()).isTrue();
        }
    }

    @Test
    void createUserPersistsACompleteEntityWithGeneratedStringId() {
        userService.createUser(UserDto.builder()
                .username("test-user")
                .password("secret")
                .email("test-user@example.com")
                .build());

        var savedUser = userRepository.findByUsername("test-user");
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotBlank();
        assertThat(savedUser.getEmail()).isEqualTo("test-user@example.com");
        assertThat(savedUser.getRole()).isEqualTo("USER");
    }

    @Test
    void h2ConsoleEndpointIsRegistered() throws Exception {
        var client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        var request = HttpRequest.newBuilder(
                        URI.create("http://localhost:" + port + "/h2-console"))
                .GET()
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertThat(response.statusCode()).isBetween(200, 399);
    }

}
