package GitHR.Entities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JSONCuteStringsObj {

    private final JsonObject jsonObject;


    public JSONCuteStringsObj(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONCuteStringsElem get(String memberName) {
        return new JSONCuteStringsElem(jsonObject.get(memberName));
    }

    @Override
    public String toString() {
        //        remove boundary quotes "string" => string
        return super.toString().replaceAll("^\"|\"$", "");
    }
}
