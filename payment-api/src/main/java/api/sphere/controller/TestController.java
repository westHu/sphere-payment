package api.sphere.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 测试API ok
 */
@Slf4j
@RestController
public class TestController {

    @GetMapping(value = "/v1/hi")
    public Mono<String> hello() {
        return Mono.just("spherePay payment");
    }

}
