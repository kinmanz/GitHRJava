package GitHR.Entities;

import com.google.gson.JsonElement;

/**
 * Created by kinmanz on 29.03.17.
 */
public class JSONCuteStringsElem {

    private final JsonElement jsonElement;

    public JSONCuteStringsElem(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    @Override
    public String toString() {
//        remove boundary quotes "string" => string
        return jsonElement.toString().replaceAll("^\"|\"$", "");
    }
}
