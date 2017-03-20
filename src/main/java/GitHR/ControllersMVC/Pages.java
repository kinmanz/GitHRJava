package GitHR.ControllersMVC;

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

@Controller
@RequestMapping("/")
class MainController {

    private final PropertyService propertyService;
    private final GitHubService gitHubService;


    @Autowired
    public MainController(PropertyService propertyService, GitHubService gitHubService) {
        this.propertyService = propertyService;
        this.gitHubService = gitHubService;
    }

    @GetMapping
    public String mainPage(HttpSession session,
                           @RequestParam(value="name", required=false, defaultValue="World") String name,
                           Model model) throws Exception{
        model.addAttribute("name", name);
        model.addAttribute("properties", propertyService.getProperties().stringPropertyNames());
        model.addAttribute("client_id", propertyService.getProperties().getProperty("client_id"));
        model.addAttribute("session_info", gitHubService.getToken() + " --- " + session.getAttribute("token"));
        model.addAttribute("token", gitHubService.getToken() + " --- " + session.getAttribute("token"));


        if (gitHubService.tokenIsSet()) {
            model.addAttribute("current_user", gitHubService.getAuthenticatedUser());
        }
        return "styled/index";
    }

    @GetMapping("/{nick}")
    public String parsePage(HttpSession session,
                            @PathVariable String nick,
                            Model model) {
        model.addAttribute("name", nick);
        model.addAttribute("session_info", gitHubService.getToken());

        return "CV";
    }

}