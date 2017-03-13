package GitHR.ControllersMVC;

import GitHR.Services.GitHubService;
import GitHR.Services.PropertyService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.simple.parser.ParseException;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Controller
@RequestMapping("/auth")
public class Authentication {

    private final PropertyService propertyService;
    private final GitHubService gitHubService;

    @Autowired
    public Authentication(PropertyService propertyService, GitHubService gitHubService) {
        this.propertyService = propertyService;
        this.gitHubService = gitHubService;
    }


    @GetMapping("/callback")
    public String callback(HttpSession session,
                           @RequestParam(value="code") String code,
                           Model model) {

        HttpResponse response;
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost postRequest = new HttpPost("https://github.com/login/oauth/access_token");
        postRequest.addHeader("Accept", "application/json");
        List<NameValuePair> postParameters = new LinkedList<>();
        postParameters.add(new BasicNameValuePair("client_id", propertyService.getProperty("client_id")));
        postParameters.add(new BasicNameValuePair("client_secret", propertyService.getProperty("client_secret")));
        postParameters.add(new BasicNameValuePair("code", code));
        try {
            postRequest.setEntity(new UrlEncodedFormEntity(postParameters));
            response = httpclient.execute(postRequest);
            String json_string = EntityUtils.toString(response.getEntity());

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(json_string);

            String access_token = (String) json.get("access_token");

            if (access_token == null) {
                throw new NullPointerException("Didn't get token !!!");
            }

            session.setAttribute("token", access_token);
            gitHubService.setToken(access_token);
        } catch (IOException | ParseException | NullPointerException e) {
            System.out.println("Authentication Error !!!");
            e.printStackTrace();
        }


        model.addAttribute("name", code);

//        redirect to main page
        return  "redirect:/";
    }

}
