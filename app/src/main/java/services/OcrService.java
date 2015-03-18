package services;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by martin_w on 18.03.2015.
 */
public class OcrService extends AsyncTask<Void, Void, Void> {
    private IServiceComplete mServiceComplete;

//  Variablen für Service
    public final String  serviceUrl = "http://api.ocrapiservice.com/1.0/rest/ocr";

    private  final int RESPONSE_OK = 200;
    private final String PARAM_IMAGE = "image";
    private final String PARAM_LANGUAGE = "language";
    private final String PARAM_APIKEY = "apikey";

    private String imagePath = "";
    private final String language = "en";
    private final String apiKey = "s4t6CdpMxG";

//    Response from Server
    private int responseCode;
    private String responseText;

    private boolean error;
    private String isbn;

    public OcrService(String imagePath){
        this.imagePath = imagePath;
    }

    public void setListener(IServiceComplete serviceComplete){
        mServiceComplete = serviceComplete;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            convertImgToText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        setError(responseCode);
        setIsbn(responseText);

        mServiceComplete.callback();
    }

    private void convertImgToText() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(serviceUrl);
        FileBody image = new FileBody(new File(imagePath));
        MultipartEntity reqEntity = new MultipartEntity();


        reqEntity.addPart(PARAM_IMAGE, image);
        reqEntity.addPart(PARAM_LANGUAGE, new StringBody(this.language));
        reqEntity.addPart(PARAM_APIKEY, new StringBody(this.apiKey));
        httppost.setEntity(reqEntity);

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();
        StringBuilder sb = new StringBuilder();
        if (resEntity != null) {
            InputStream stream = resEntity.getContent();
            byte bytes[] = new byte[4096];
            int numBytes;
            while ((numBytes=stream.read(bytes))!=-1) {
                if (numBytes!=0) {
                    sb.append(new String(bytes, 0, numBytes));
                }
            }
        }

        setResponseCode(response.getStatusLine().getStatusCode());
        setResponseText(sb.toString());
    }

    private void setResponseText(String responseText) {
        this.responseText = responseText;
    }
    private void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    private void setError(int responseCode){
        if(responseCode == RESPONSE_OK){
            error = false;
        }
        else{
            error = true;
        }
    }
    private void setIsbn(String responseText){
        responseText = responseText.toUpperCase();
        isbn = responseText.replace("ISBN", "").trim();
    }

    public String getIsbn() {
        return this.isbn;
    }
    public boolean isError() {
        return this.error;
    }



}
