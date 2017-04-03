package GitHR.ControllersMVC;

import GitHR.Entities.JSONCuteStringsObj;
import GitHR.Services.GitHubService;
import GitHR.Services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/")
class MainController {

    private final PropertyService propertyService;
    private final GitHubService gitHubService;
    private final AtomicInteger atomicInteger = new AtomicInteger(1);


    @Autowired
    public MainController(PropertyService propertyService, GitHubService gitHubService) {
        this.propertyService = propertyService;
        this.gitHubService = gitHubService;
    }

    @GetMapping
    public String mainPage(HttpSession session,
                           @RequestParam(value="name", required=false, defaultValue="World") String name,
                           Model model) throws Exception{

        model.addAttribute("service", gitHubService);
        model.addAttribute("authuser", new JSONCuteStringsObj(gitHubService.getAuthenticatedUser()));

        session.setAttribute("check", gitHubService.getToken());

        model.addAttribute("ssession", session);

//        return "main";
        return "styled/index";
    }

    @GetMapping("/test/test")
    public String testPage(HttpSession session,
                            Model model) throws Exception {

        model.addAttribute("token", gitHubService.getToken());

        return "test";
    }

    @GetMapping("/{nick}")
    public String parsePage(HttpSession session,
                            @PathVariable String nick,
                            Model model) throws Exception {

        model.addAttribute("service", gitHubService);
        model.addAttribute("nick", nick);
        model.addAttribute("authuser", new JSONCuteStringsObj(gitHubService.getAuthenticatedUser()));
//        model.addAttribute("targetuser", new JSONCuteStringsObj(gitHubService.getFullCvJSON("")));

        return "styled/cv";
    }

}