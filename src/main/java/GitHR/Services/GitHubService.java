package GitHR.Services;

import GitHR.Entities.JSONObjWrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Future;

import static java.lang.System.exit;

@Service
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GitHubService {

    @Autowired
    public GitHubService (ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
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
    private Object makeRequest(final String urlPath) throws IOException{
        HttpResponse response;
        JSONParser parser = new JSONParser();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet postRequest = new HttpGet(API_URL + urlPath);
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Authorization", String.format("token %s", token));

        try {
            response = httpclient.execute(postRequest);
            String json_string = EntityUtils.toString(response.getEntity());

            return parser.parse(json_string);
        } catch (IOException | ParseException | NullPointerException e) {
            System.out.println("GitHub Service request ERROR !!!");
            e.printStackTrace();
            throw new IOException(String.format("GitHubService.makeRequest( %s ) exception", urlPath));
        }
    }

    public JSONObject getAuthenticatedUser() throws IOException {
        return (JSONObject) makeRequest("/user");
    }

    public JSONObject getUser(String login) throws IOException{
        return (JSONObject) makeRequest("/users/" + login);
    }

//    expect repoFullName, for example d3/d3 or kinmanz/working
    public JSONArray getRepoContributers(String repoFullName) throws IOException {
        return ((JSONArray) makeRequest("/repos/" + repoFullName + "/stats/contributors"));
    }


    /*
    *  expect : repoFullName => for example d3/d3 or kinmanz/working
    *
    *  return: JSONObject corresponding to specified user,
    *  or null if that user doesn't have contribution to that repo
    * */
    public JSONObject getRepoContributionForProfile(String repoFullName, String userLogin) throws IOException {
        JSONArray jsonArray = getRepoContributers(repoFullName);
        for (ListIterator iterator = jsonArray.listIterator(jsonArray.size()); iterator.hasPrevious();) {
            final Object object = iterator.previous();
            JSONObjWrapper jsonObjWrapper = new JSONObjWrapper((JSONObject) object);
            if (userLogin.equals(jsonObjWrapper.getObj("author").getField("login"))) {
                return  (JSONObject) object;
            }
        }
        return null;
    }

    @Async("taskExecutorAPICalls")
    public Future<JSONObject> getRepoContributionForProfileFuture(String repoFullName, String userLogin) throws IOException {
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
    public JSONObject getProfileInfoGraphQL(String login) throws IOException {

        HttpResponse response;
        JSONParser parser = new JSONParser();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost postRequest = new HttpPost("https://api.github.com/graphql");
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Authorization", String.format("token %s", token));

        try {
            JSONObject obj = new JSONObject();
            obj.put("query", String.format(getProfileInfoGraphQLQuery, login));
            HttpEntity entity = new StringEntity(obj.toString());
            postRequest.setEntity(entity);

            response = httpclient.execute(postRequest);
            String json_string = EntityUtils.toString(response.getEntity());

           JSONObject jsonObject = (JSONObject) parser.parse(json_string);
           return (JSONObject)((JSONObject)jsonObject.get("data")).get("user");

        } catch (IOException | ParseException | NullPointerException e) {
            System.out.println("GraphQL Query ERROR Error !!!");
            e.printStackTrace();
            throw new IOException("GraphQL Query ERROR Error !!!");
        }
    }

}
