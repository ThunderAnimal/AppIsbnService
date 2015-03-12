package services;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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

        System.out.println(json);
        return json;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);

        mServiceComplete.callback();
    }
}
