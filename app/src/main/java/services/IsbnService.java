package services;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Created by martin_w on 12.03.2015.
 */
public class IsbnService extends AsyncTask<Void, Void, String> {
    private final  String serviceUrl = "http://xisbn.worldcat.org/webservices/xid/isbn/";
    private  final  String serviceMethod = "?method=getMetadata&format=json&fl=*";

    private IServiceComplete mServiceComplete;
    private String isbn;

    public IsbnService(String isbn){
        this.isbn = isbn;
    }
    public void setListener(IServiceComplete serviceComplete){
        mServiceComplete = serviceComplete;
    }


    @Override
    protected String doInBackground(Void... params) {
        String json = "";
        String inputLine;

        try {
            URL url = new URL(serviceUrl + isbn + serviceMethod);
            BufferedReader in = new BufferedReader(new InputStreamReader (url.openStream()));
            while ((inputLine = in.readLine()) != null){
                json += inputLine;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);

        mServiceComplete.callback(json);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean isError(String json,TextView Error){
        boolean error;
        String errorText;

        String stat;
        try {
            stat = new JSONObject(json).getString("stat");

            if(Objects.equals(stat, "ok")){
                errorText = "";
                error = false;
            }else {
                if(Objects.equals(stat, "unknownId"))
                    errorText = "ISBN is unknown to xISBN service";
                else if(Objects.equals(stat, "invalidId"))
                    errorText = "ISBN is invalid";
                else if(Objects.equals(stat, "overlimit"))
                    errorText = "Service is used over limit";
                else if(Objects.equals(stat, "unknownField"))
                    errorText = "request field is not supported";
                else if(Objects.equals(stat, "unknownFormat"))
                    errorText = "request format is not supported";
                else if(Objects.equals(stat, "unknownLibrary"))
                    errorText = "request library is not supported";
                else if(Objects.equals(stat, "unknownMethod"))
                    errorText = "request method is not supported";
                else if(Objects.equals(stat, "invalidAffiliateId"))
                    errorText = " invalid affiliate id";
                else if(Objects.equals(stat, "invalidHash"))
                    errorText = "invalid hash";
                else if(Objects.equals(stat, "invalidToken"))
                    errorText = " invalid access token";
                else
                    errorText = "unknown error";

                error = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
            errorText = "Invalid JSON Object returned";

        }

        Error.setText(errorText);
        return error;
    }

}
