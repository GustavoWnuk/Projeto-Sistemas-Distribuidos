package meAjudaPFVR;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Utils {
    private static final Gson gson = new Gson();

    public static JsonObject createRequest(String operation) {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("operation", operation);
        return requestJson;
    }

    public static JsonObject parseJson(String json) {
        return gson.fromJson(json, JsonObject.class);
    }

    public static String toJsonString(JsonObject jsonObject) {
        return gson.toJson(jsonObject);
    }

    public static String sendRequest(JsonObject requestJson, PrintWriter out, BufferedReader in) throws IOException {
        out.println(requestJson.toString());
        return in.readLine();
    }

    public static JsonObject createResponse(String operation, String status, String token) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("operation", operation);
        responseJson.addProperty("status", status);
        JsonObject data = new JsonObject();
        data.addProperty("token", token);
        responseJson.add("data", data);
        return responseJson;
    }
}

