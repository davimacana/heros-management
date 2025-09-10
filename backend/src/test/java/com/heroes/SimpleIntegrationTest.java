package com.heroes;

import com.heroes.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = HeroesManagementApplication.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "logging.level.org.springframework=DEBUG",
    "logging.level.org.hibernate=DEBUG",
    "spring.jpa.show-sql=true"
})
class SimpleIntegrationTest {

    @Test
    void contextLoads() {
    }
}
