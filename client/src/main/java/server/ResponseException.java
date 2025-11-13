package server;

import com.google.gson.Gson;

import java.util.HashMap;

public class ResponseException extends RuntimeException {
    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }

    public static ResponseException fromJson(String json) {
        var body = new Gson().fromJson(json, HashMap.class);
        return new ResponseException(body.get("message").toString());
    }
}
