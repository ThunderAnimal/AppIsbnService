package com.ebusiness.isbnservice.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import services.IServiceComplete;
import services.IsbnService;


public class MainActivity extends ActionBarActivity {

    private Button mButtonIsbn;
    private EditText mTextIsbn;
    private ProgressBar mProgressIsbn;
    private IsbnService myIsbnService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Elemente mit der View verdrahten
        mButtonIsbn = (Button) findViewById(R.id.btnloadIsbnData);
        mTextIsbn = (EditText) findViewById(R.id.inputIsbn);
        mProgressIsbn = (ProgressBar) findViewById(R.id.progressBarLoadIsbn);


        // Anzeige
        mProgressIsbn.setVisibility(View.INVISIBLE);

        mButtonIsbn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mButtonIsbn.setEnabled(false);
                mProgressIsbn.setVisibility(View.VISIBLE);

                myIsbnService = new IsbnService(mTextIsbn.getText().toString());
                myIsbnService.setListener(new IServiceComplete() {
                    @Override
                    public void callback() {
                        mButtonIsbn.setEnabled(true);
                        mProgressIsbn.setVisibility(View.INVISIBLE);
                        mTextIsbn.setText("");
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
