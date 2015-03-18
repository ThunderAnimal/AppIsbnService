package com.ebusiness.isbnservice.app;


import adapter.TwoTextArrayAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import listView.*;
import services.IServiceComplete;
import services.IsbnService;
import services.OcrService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private Button mButtonIsbn;
    private EditText mTextIsbn;
    private ProgressBar mProgressIsbn;
    private TextView mTextError;
    private ListView mListView;

    private IsbnService myIsbnService;
    private OcrService myOcrService;

    private AlertDialog.Builder builder;

    private List<Item> items = new ArrayList<Item>();
    private TwoTextArrayAdapter adapter;

    private final int IMAGE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Elemente mit der View verdrahten
        getViewElemnts();

        //Adapter für ListView erstellen
        setAdapter();

        //Builder für Alter Dialog erstellen
        setDialogBuilder();

        //Anzeige für Startview
        setStartView();

        mButtonIsbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLoadIsbn();
            }
        });
    }

    private void onClickPickImg(){
        //Start Activity to Pick Image
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), IMAGE_PICKER_REQUEST);
    }

    private  void onClickLoadIsbn(){
        String text = mTextIsbn.getText().toString();
        if(text.length() == 0){
            builder.setMessage("NO input");
            builder.create().show();
        }
        else{
            executeIsbnService(text);
        }
    }

    private void getViewElemnts(){
        //Elemente mit der View verdrahten
        mButtonIsbn = (Button) findViewById(R.id.btnloadIsbnData);
        mTextIsbn = (EditText) findViewById(R.id.inputIsbn);
        mProgressIsbn = (ProgressBar) findViewById(R.id.progressBarLoadIsbn);
        mTextError = (TextView) findViewById(R.id.textError);
        mListView = (ListView) findViewById(R.id.listView);

    }

    private void setDialogBuilder(){
        //Alert Dialog
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });
    }

    private void setAdapter(){
        adapter = new TwoTextArrayAdapter(this, items);
        mListView.setAdapter(adapter);

    }

    private void changeIsbnDataList(ArrayList<ItemIsbnData> listIsbnData){
        for(int i = 0; i < listIsbnData.size();i++){
            items.add(new ListItem(listIsbnData.get(i)));
        }
    }

    private  void changeIsbnEditionList(ArrayList<ItemEditions> listEditions){
        if(listEditions.size() <= 0)
            return;

        items.add(new ListHeader("Editions"));
        for(int i = 0; i < listEditions.size();i++){
            items.add(new ListItem(listEditions.get(i)));
        }
    }

    private void setViewOkIsbn(){
        mListView.setVisibility(View.VISIBLE);
        mTextError.setVisibility(View.INVISIBLE);
        mTextIsbn.setText("");
    }

    private void setViewErrorIsbn(){
        mTextError.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.INVISIBLE);
    }

    private void setStartView(){
        // Anzeige
        mProgressIsbn.setVisibility(View.INVISIBLE);
        mTextError.setVisibility(View.INVISIBLE);
        mListView.setVisibility(View.INVISIBLE);
    }

    private void setStartLoadView(){
        mButtonIsbn.setEnabled(false);
        mProgressIsbn.setVisibility(View.VISIBLE);
    }

    private void setStopLoadView(){
        mButtonIsbn.setEnabled(true);
        mProgressIsbn.setVisibility(View.INVISIBLE);
    }

    private void executeIsbnService(String isbn){
        setStartLoadView();
        myIsbnService = new IsbnService(isbn);
        myIsbnService.setListener(new IServiceComplete() {
            @Override
            public void callback() {
                //Prüfen ob Error oder nicht
                if(myIsbnService.isError()){
                    mTextError.setText(myIsbnService.getErrorText());
                    setViewErrorIsbn();
                }else {
                    items.clear();
                    changeIsbnDataList(myIsbnService.getIsbnDataList());
                    changeIsbnEditionList(myIsbnService.getIsbnEditions());
                    adapter.notifyDataSetChanged();
                    setViewOkIsbn();
                }
                setStopLoadView();
                if(myIsbnService.isNewEdition()){
                    builder.setMessage("Book is deprecated \nnew edition available!");
                    builder.create().show();
                }
            }
        });
        myIsbnService.execute();
    }

    private void executeOcrService(String filePath){
        setStartLoadView();
        myOcrService = new OcrService(filePath);
        myOcrService.setListener(new IServiceComplete() {
            @Override
            public void callback() {
                //Prüfen ob Error oder nicht
                if(myOcrService.isError()){
                    mTextError.setText("Can't read ISBN");
                    setViewErrorIsbn();
                }else {
                    mTextIsbn.setText(myOcrService.getIsbn());
                    executeIsbnService(myOcrService.getIsbn());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_camera){
            onClickPickImg();
        }
        if(id == R.id.action_about){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK){
            executeOcrService(getRealPathFromURI(data.getData()));
        }
    }

    private String getRealPathFromURI(final Uri contentUri) {
        final String[] proj = {MediaStore.Images.Media.DATA};
        final Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}
