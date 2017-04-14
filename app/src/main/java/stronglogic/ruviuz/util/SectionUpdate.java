package stronglogic.ruviuz.util;

import android.os.Handler;

import org.json.JSONObject;

/**
 * Created by logicp on 4/12/17.
 */

public class SectionUpdate implements Runnable {

    private String authToken, reqMethod, contentType;
    private Handler handler;

    private int sectionId;

    private JSONObject payload;

    public SectionUpdate() {

    }

    public SectionUpdate(Handler handler, String contentType, String reqMethod, String authToken) {
        this.handler = handler;
        this.contentType = contentType;
        this.reqMethod = reqMethod;
        this.authToken = authToken;
    }

    @Override
    public void run() {

        try {
            sendRequest();
        } catch (RuvThreadException e) {
            e.printStackTrace();
        }

    }


    public void sendRequest() throws RuvThreadException {
        if (payload == null || payload.toString().equals("")) {
            throw new RuvThreadException();
        } else {
            RuuvHttpClient ruvClient = new RuuvHttpClient(handler, authToken, reqMethod);

            ruvClient.setGetResponse(true);
            ruvClient.setDestination("/section/update/" + sectionId);
            ruvClient.setContentType("application/json");
            ruvClient.setReqMethod("POST");
            ruvClient.setPayload(this.payload);
        }
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public void setId(int id) {
        this.sectionId = id;
    }

    private class RuvThreadException extends Exception {

    }
}
