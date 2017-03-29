package GitHR.ControllersREST;

import GitHR.Services.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session")
public class SessionInfoController {

    private final GitHubService gitHubService;

    @Autowired
    public SessionInfoController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/has_token")
    public String greeting() {
        return gitHubService.tokenIsSet() ? "true" : "false";
    }
}

