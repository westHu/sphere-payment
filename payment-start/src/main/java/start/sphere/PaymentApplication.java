package start.sphere;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.time.LocalDateTime;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@EnableWebFlux
public class PaymentApplication implements CommandLineRunner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("===> Congratulations paysphere!");
        log.info("===> Paysphere springApplication start success, time:{}", LocalDateTime.now());
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configure(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

}
