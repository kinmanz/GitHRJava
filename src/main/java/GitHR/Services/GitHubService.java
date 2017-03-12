package GitHR.Services;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public JSONObject makeRequest(final String urlPath) {
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
//            throw new IOException(String.format("GitHubService.makeRequest( %s ) exception", urlPath));
        }
        return null;
    }

    public JSONObject getAuthenticatedUser() {
        return makeRequest("/user");
    }

    public JSONObject getUser(String login) {
        return makeRequest("/users/" + login);
    }

}
