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

    public JSONCuteStringsObj getAsJsonObject(String memberName) {
        return new JSONCuteStringsObj(jsonObject.getAsJsonObject(memberName));
    }

    /*
    * return "" if member does not exist
    * otherwise toString() representation of requested field
    * */
    public String getAsString(String memberName) {
        JsonElement member = jsonObject.get(memberName);
        return member != null ? member.getAsString() : "";
    }

    @Override
    public String toString() {
        //        remove boundary quotes "string" => string
        return super.toString().replaceAll("^\"|\"$", "");
    }
}
