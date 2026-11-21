package com.sarvatra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

    private final RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        final ResponseEntity<String> forEntity = restTemplate.getForEntity("https://9c3deb1b-bfc2-4486-a165-4e9254ce6771.mock.pstmn.io/employees", String.class);
//        log.info("data get is :- \n{}", forEntity.getBody());

        final ResponseEntity<String> forEntity = restTemplate.getForEntity("https://localhost:13022/students/1", String.class);
        log.info("data get is :- \n{}", forEntity.getBody());
    }
}
