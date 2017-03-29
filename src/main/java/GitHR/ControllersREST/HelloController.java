package GitHR.ControllersREST;

import GitHR.Entities.Hello;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

//@RequestMapping(method=GET)
@RestController
@RequestMapping("/api/test")
public class HelloController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/hello")
    public Hello greeting(@RequestParam(value="name", defaultValue="GitHR World") String name) {
        return new Hello(counter.incrementAndGet(),
                String.format(template, name));
    }
}