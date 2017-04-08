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

    public JSONCuteStringsObj getAsJsonObject() {
        return new JSONCuteStringsObj(jsonElement.getAsJsonObject());
    }

    @Override
    public String toString() {
//        remove boundary quotes "string" => string
        if (jsonElement == null) return "";
        return jsonElement.toString().replaceAll("^\"|\"$", "");
    }
}
