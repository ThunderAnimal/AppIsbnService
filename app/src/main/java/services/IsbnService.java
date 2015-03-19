package services;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;
import listView.ItemEditions;
import listView.ItemIsbnData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by martin_w on 12.03.2015.
 */
public class IsbnService extends AsyncTask<Void, Void, String> {
    private final  String serviceUrl = "http://xisbn.worldcat.org/webservices/xid/isbn/";
    private  final  String serviceMethod = "?method=getEditions&format=json&fl=*";

    private IServiceComplete mServiceComplete;
    private String isbn;

    private boolean error;
    private String errorText;
    private ArrayList<ItemIsbnData> isbnData = new ArrayList<ItemIsbnData>();
    private ArrayList<ItemEditions> isbnEditions = new ArrayList<ItemEditions>();
    private int currentEdition;
    private int maxEtition;

    public IsbnService(String isbn){
        this.isbn = isbn;
        maxEtition = 0;
        currentEdition = 0;
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

        setError(json);
        setErrorText(json);
        setIsbnDataList(json);
        setIsbnEditions(json);

        mServiceComplete.callback();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setError(String json){
        String stat;
        try {
            stat = new JSONObject(json).getString("stat");

            if(Objects.equals(stat, "ok")){
                error =  false;
            }else {
                error = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            error = true;
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setErrorText(String json){
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
    }
    private void setIsbnDataList(String json){
        JSONObject isbnJsonObject;
        ItemEditions edition;
        isbnData.clear();

        try{
            isbnJsonObject = new JSONArray(new JSONObject(json).getString("list")).getJSONObject(0);
            try {
                isbnData.add(new ItemIsbnData ("TITLE", isbnJsonObject.getString("title")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("AUTOR", isbnJsonObject.getString("author")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("PUBLISHER", isbnJsonObject.getString("publisher")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("LANGUAGE", isbnJsonObject.getString("lang")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("YEAR", isbnJsonObject.getString("year")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("EDITION", isbnJsonObject.getString("ed")));

                //Aktuelle Versionsnummer setzten
                edition = new ItemEditions(isbnJsonObject.getString("ed"),0,"");
                currentEdition = edition.getEditionNr();
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("FORM", isbnJsonObject.getString("form")));
            }catch (JSONException e){
                e.printStackTrace();
            }
            try {
                isbnData.add(new ItemIsbnData("ISBN", (String) isbnJsonObject.getJSONArray("isbn").get(0)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


    }
    private  void setIsbnEditions(String json){
        JSONArray isbnEditionsArray;
        JSONObject isbnJsonObject;
        ItemEditions edition;
        isbnEditions.clear();
        try {
            isbnEditionsArray  = new JSONArray(new JSONObject(json).getString("list"));
            for (int i = 1; i < isbnEditionsArray.length(); i++){
                isbnJsonObject = isbnEditionsArray.getJSONObject(i);
                try {
                    edition = new ItemEditions(isbnJsonObject.getString("ed"),new Integer(isbnJsonObject.getString("year")),isbnJsonObject.getJSONArray("isbn").get(0).toString());

                    isbnEditions.add(edition);

                    //Maximale Versionsnummer setzten
                    if(edition.getEditionNr() > maxEtition){
                        maxEtition = edition.getEditionNr();
                    }
                }catch (JSONException e){
                    edition = new ItemEditions("1st ed.",new Integer(isbnJsonObject.getString("year")),isbnJsonObject.getJSONArray("isbn").get(0).toString());
                    isbnEditions.add(edition);
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(isbnEditions);
    }

    public boolean isError(){
        return this.error;
    }
    public String getErrorText(){
        return this.errorText;
    }
    public boolean isNewEdition(){
        return(currentEdition<maxEtition);
    }
    public ArrayList<ItemIsbnData> getIsbnDataList(){
        return this.isbnData;
    }
    public ArrayList<ItemEditions> getIsbnEditions(){
        return this.isbnEditions;
    }

}
