package GitHR.Services;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.System.exit;

@Service
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GitHubService {

    @Autowired
    public GitHubService (ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;

    // If was a restart of application, all beans are died, so we need to reconstruct them
        Object tokenInSession = getSession().getAttribute("token");
        if (!tokenIsSet() && tokenInSession != null) {
//         but session has token inside
           setToken((String) tokenInSession);
        }
    }

//    It can be done just by using @Autowired HttpSession httpSession
//    (put as additional parameter in constructor)
//    but because of testing, I have decided to choose way bellow
    public static HttpSession getSession() {
//        Return the RequestAttributes currently bound to the thread.
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(); // true == allow create
    }

    private String token = null;
    private final String API_URL = "https://api.github.com";

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public boolean tokenIsSet() {
        return  token != null;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenIfNotPresent(String token) {
        if (!tokenIsSet())
            this.token = token;
    }

    public String getToken() {
        return token;
    }

    /*
    * Provide urlPath with leading slash !!!
    * for example: /user
    * */
    private JsonElement makeRequest(final String urlPath) throws IOException {
        HttpResponse response;
        JsonParser parser = new JsonParser();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet postRequest = new HttpGet(API_URL + urlPath);
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Authorization", String.format("token %s", token));

        try {
            response = httpclient.execute(postRequest);
            String json_string = EntityUtils.toString(response.getEntity());
            JsonElement jsonElement = parser.parse(json_string);

            return jsonElement;
        } catch (NullPointerException e) {
            System.out.println("GitHub Service request ERROR !!!");
            e.printStackTrace();
            throw new IOException(String.format("GitHubService.makeRequest( %s ) exception", urlPath));
        }
    }

    public JsonObject getAuthenticatedUser() throws IOException {
        return makeRequest("/user").getAsJsonObject();
    }

    public JsonObject getUser(String login) throws IOException{
        return  makeRequest("/users/" + login).getAsJsonObject();
    }

//    expect repoFullName, for example d3/d3 or kinmanz/working
    public JsonArray getRepoContributers(String repoFullName) throws IOException {

        JsonElement result = makeRequest("/repos/" + repoFullName + "/stats/contributors");

        if (result.isJsonObject()) {
            JsonArray arr = new JsonArray();
            arr.add(result);
            return arr;
        }

        return result.getAsJsonArray();
    }


    /*
    *  expect : repoFullName => for example d3/d3 or kinmanz/working
    *
    *  return: JSONObject corresponding to specified user,
    *  or null if that user doesn't have contribution to that repo
    * */
    public JsonObject getRepoContributionForProfile(String repoFullName, String userLogin) throws IOException {
        JsonArray jsonArray = getRepoContributers(repoFullName);
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            try {
                if (userLogin.equals(jsonObject.getAsJsonObject("author").get("login").getAsString())) {
                    return  jsonObject;
                }
            } catch (NullPointerException e) {
                System.out.println("Error: " + repoFullName + " ============= " + userLogin);
                e.printStackTrace();
            }

        }
        return null;
    }

    @Async("taskExecutorAPICalls")
    public Future<JsonObject> getRepoContributionForProfileFuture(String repoFullName, String userLogin) throws IOException {
        return new AsyncResult<>(getRepoContributionForProfile(repoFullName, userLogin));
    }

//    @Async("taskExecutorAPICalls")
//    public Future<?> getWait() throws Exception{
//        return new AsyncResult<>(waitTest());
//    }
//
//    public Object waitTest() throws Exception{
//        System.out.println("!!!!!-!!!!!!!-!!!!!****");
//        Thread.sleep(10000);
//        return null;
//    }

    private static final String getProfileInfoGraphQLQuery;
    static {
        byte[] encoded = new byte[] {};
        try {
            encoded = Files.readAllBytes(Paths.get("./src/main/resources/queries/getUsertInfoGraphQL.txt"));
        } catch (IOException e) {
            System.out.println("Can start system, cause is:");
            System.out.println("Query GitHubService.getProfileInfoGraphQL NOT SET !!!");
            e.printStackTrace();
            exit(-1);
        }
        getProfileInfoGraphQLQuery = new String(encoded, Charset.defaultCharset());
        System.out.println("Query GitHubService.getProfileInfoGraphQL set successfully!");
    }
    public JsonObject getProfileInfoGraphQL(String login) throws IOException {

        HttpResponse response;
        JsonParser parser = new JsonParser();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost postRequest = new HttpPost("https://api.github.com/graphql");
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Authorization", String.format("token %s", token));

        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("query", String.format(getProfileInfoGraphQLQuery, login));
            HttpEntity entity = new StringEntity(obj.toString());
            postRequest.setEntity(entity);

            response = httpclient.execute(postRequest);
            String json_string = EntityUtils.toString(response.getEntity());

           JsonObject jsonObject = parser.parse(json_string).getAsJsonObject();
           return jsonObject.getAsJsonObject("data");

        } catch (IOException | NullPointerException e) {
            System.out.println("GraphQL Query ERROR Error !!!");
            e.printStackTrace();
            throw new IOException("GraphQL Query ERROR Error !!!");
        }
    }


    public JsonObject getFullCvJSON(String login) throws IOException{
        JsonObject cv = getProfileInfoGraphQL(login);
        JsonArray repos = cv.getAsJsonObject("user")
                .getAsJsonObject("contributedRepositories")
                .getAsJsonArray("edges");

        Map <String, Future<JsonObject>> futureMap = new HashMap<>(200);

        for (JsonElement repo: repos) {
            JsonObject obj = repo.getAsJsonObject();
            String repoName = obj.getAsJsonObject("node").get("name").getAsString();
            String ownerLogin = obj.getAsJsonObject("node").getAsJsonObject("owner")
                    .get("login").getAsString();

            futureMap.put(repoName, getRepoContributionForProfileFuture(ownerLogin + "/" + repoName, login));
        }

        try {
            for (JsonElement jsonElement: repos) {
                JsonObject obj = jsonElement.getAsJsonObject();
                String repoName = obj.getAsJsonObject("node").get("name").getAsString();

                JsonObject contribute = futureMap.get(repoName).get();
                obj.getAsJsonObject("node").add("contribute", contribute);
            }
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Concurrency Exception getFullCvJSON() !!!");
            e.printStackTrace();
        }
        return cv;
    }


}
