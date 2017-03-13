package GitHR.Services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.System.exit;
import static java.lang.System.setOut;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GitHubService {

    private final String API_URL = "https://api.github.com";
    private String token = null;

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
    private JSONObject makeRequest(final String urlPath) throws IOException{
        HttpResponse response;
        JSONParser parser = new JSONParser();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet postRequest = new HttpGet(API_URL + urlPath);
        postRequest.addHeader("Accept", "application/json");
        postRequest.addHeader("Authorization", String.format("token %s", token));

        try {
            response = httpclient.execute(postRequest);
            String json_string = EntityUtils.toString(response.getEntity());

            return (JSONObject) parser.parse(json_string);
        } catch (IOException | ParseException | NullPointerException e) {
            System.out.println("GitHub Service request ERROR !!!");
            e.printStackTrace();
            throw new IOException(String.format("GitHubService.makeRequest( %s ) exception", urlPath));
        }
    }

    public JSONObject getAuthenticatedUser() throws IOException{
        return makeRequest("/user");
    }

    public JSONObject getUser(String login) throws IOException{
        return makeRequest("/users/" + login);
    }


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

            return (JSONObject) parser.parse(json_string);

        } catch (IOException | ParseException | NullPointerException e) {
            System.out.println("GraphQL Query ERROR Error !!!");
            e.printStackTrace();
            throw new IOException("GraphQL Query ERROR Error !!!");
        }
    }

}
