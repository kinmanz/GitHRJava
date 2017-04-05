package GitHR.Services;

import com.google.gson.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.Future;

@RunWith(MockitoJUnitRunner.class)
public class GitHubServiceTest {

    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private GitHubService gitHubService = new GitHubService(threadPoolTaskExecutor);
    private PropertyService propertyService = new PropertyService();

    @BeforeClass
    public static void runOnceBeforeClass() {
        GitHubService.TESTED = true;
    }

    @Before
    public void runOnceBeforeInstance() {
        threadPoolTaskExecutor.setCorePoolSize(7);
        threadPoolTaskExecutor.initialize();
        gitHubService.setToken(propertyService.getProperty("test.token"));
    }

    @Test
    public void getAuthenticatedUserTest() throws Exception {
        JsonObject jsonObject = gitHubService.getAuthenticatedUser();
        System.out.println(gson.toJson(jsonObject));

        jsonObject = gitHubService.getUser("ALEXSSS");
        System.out.println(gson.toJson(jsonObject));

        System.out.println(jsonObject.get("login"));
        System.out.println(jsonObject.get("login").toString().replaceAll("^\"|\"$", ""));

    }

    @Test
    public void getGraphQLUserInfoTest() throws Exception {
        JsonObject jsonObject = gitHubService.getProfileInfoGraphQL("ALEXSSS");
        System.out.println(gson.toJson(jsonObject));
    }

    @Test
    public void getRepoContributors() throws Exception {
        JsonArray jsonArray = gitHubService.getRepoContributers("d3/d3");
        System.out.println(gson.toJson(jsonArray));
    }

    @Test
    public void getRepoContributorsByLogin() throws Exception {
        JsonObject jsonObject = gitHubService.getRepoContributionForProfile("d3/d3", "mbostock");
        System.out.println(gson.toJson(jsonObject));
    }


    @Test
    public void getFullCvJSON() throws Exception {
        JsonObject jsonObject = gitHubService.getFullCvJSON("ALEXSSS");
//        JsonObject jsonObject = gitHubService.getFullCvJSON("avgaydashenko");
        System.out.println(gson.toJson(jsonObject));
    }

    @Test
    public void getFullCvJSONMock() throws Exception {
        JsonObject jsonObject = gitHubService.getFullCvJSONMock("ALEXSSS");
        System.out.println(gson.toJson(jsonObject));
    }
}



