package com.mgumieniak.architecture.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        count = 3,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class ArchitectureWebappApplicationTests {

    @Test
    void contextLoads() {
    }

}
