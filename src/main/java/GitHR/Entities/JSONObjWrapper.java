package GitHR.Entities;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONObjWrapper {
    private final JSONObject jsonObject;

    public JSONObjWrapper(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObjWrapper(Object jsonObject) {
        this.jsonObject = (JSONObject) jsonObject;
    }

    public JSONObject getInstance() {
        return jsonObject;
    }

    public Object getField(String fieldName) {
        return jsonObject.get(fieldName);
    }

    public JSONObjWrapper getObj(String fieldName) {
        return new JSONObjWrapper((JSONObject) jsonObject.get(fieldName));
    }

    public JSONArray getArr(String fieldName) {
        return (JSONArray) jsonObject.get(fieldName);
    }
}

