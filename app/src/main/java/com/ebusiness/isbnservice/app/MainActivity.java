package com.ebusiness.isbnservice.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import services.IServiceComplete;
import services.IsbnService;


public class MainActivity extends ActionBarActivity {

    private Button mButtonIsbn;
    private EditText mTextIsbn;
    private ProgressBar mProgressIsbn;
    private IsbnService myIsbnService;
    private TextView mTextError;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Elemente mit der View verdrahten
        mButtonIsbn = (Button) findViewById(R.id.btnloadIsbnData);
        mTextIsbn = (EditText) findViewById(R.id.inputIsbn);
        mProgressIsbn = (ProgressBar) findViewById(R.id.progressBarLoadIsbn);
        mTextError = (TextView) findViewById(R.id.textError);

        //Alert Dialog
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.cancel();
            }
        });



        // Anzeige
        mProgressIsbn.setVisibility(View.INVISIBLE);
        mTextError.setVisibility(View.INVISIBLE);

        mButtonIsbn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = mTextIsbn.getText().toString();
                if(text.length() == 0){
                    builder.setMessage("NO input");
                    builder.create().show();
                    return;
                }

                mButtonIsbn.setEnabled(false);
                mProgressIsbn.setVisibility(View.VISIBLE);

                myIsbnService = new IsbnService(text);
                myIsbnService.setListener(new IServiceComplete() {
                    @Override
                    public void callback(String Json) {
                        //Prüfen ob Error oder nicht
                        if(myIsbnService.isError(Json,mTextError)){
                            mTextError.setVisibility(View.VISIBLE);
                        }else {
                            mTextError.setVisibility(View.INVISIBLE);
                            mTextIsbn.setText("");
                        }
                        mButtonIsbn.setEnabled(true);
                        mProgressIsbn.setVisibility(View.INVISIBLE);

                    }
                });
                myIsbnService.execute();
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

        return super.onOptionsItemSelected(item);
    }
}
