package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@AutoConfigureRestTestClient
class UserControllerTest {
    @Autowired
    private RestTestClient restTestClient;

    @Test
    void shouldReturnOk(){
        restTestClient.get().uri("/users/all")
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }
}