package services;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by martin_w on 12.03.2015.
 */
public class IsbnService extends AsyncTask<Void, Void, String> {
    private final  String serviceUrl = "http://xisbn.worldcat.org/webservices/xid/isbn/";
    private  final  String serviceMethod = "?method=getEditions&format=json&fl=*";

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
    public String getErrorText(String json){
        String errorText = "";
        String stat;

        try {
            stat = new JSONObject(json).getString("stat");

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
                errorText = "Connection Problems";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorText;


    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean isError(String json){
        String stat;
        try {
            stat = new JSONObject(json).getString("stat");

            if(Objects.equals(stat, "ok")){
                return  false;
            }else {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }
    public ArrayList<String[]> getIsbnDataList(String json){
        JSONObject isbnJsonObject;
        ArrayList<String[]> isbnData = new ArrayList<String[]>();

        try {
            isbnJsonObject = new JSONArray(new JSONObject(json).getString("list")).getJSONObject(0);
            isbnData.add(new String[]{"title", isbnJsonObject.getString("title")});
            isbnData.add(new String[]{"autor", isbnJsonObject.getString("author")});
            isbnData.add(new String[]{"publischer", isbnJsonObject.getString("publisher")});
            isbnData.add(new String[]{"language", isbnJsonObject.getString("lang")});
            isbnData.add(new String[]{"year", isbnJsonObject.getString("year")});
            isbnData.add(new String[]{"edition", isbnJsonObject.getString("ed")});
            isbnData.add(new String[]{"form", isbnJsonObject.getString("form")});
            isbnData.add(new String[]{"isbn", isbnJsonObject.getString("isbn")});
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isbnData;
    }

}
