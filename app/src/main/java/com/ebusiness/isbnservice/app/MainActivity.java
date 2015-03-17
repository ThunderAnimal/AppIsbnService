package com.ebusiness.isbnservice.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import services.IServiceComplete;
import services.IsbnService;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private Button mButtonIsbn;
    private EditText mTextIsbn;
    private ProgressBar mProgressIsbn;
    private IsbnService myIsbnService;
    private TextView mTextError;
    private ListView mListView;

    private AlertDialog.Builder builder;
    private ArrayList<String[]> isbnData = new ArrayList<String[]>();
    private ArrayAdapter<String[]> adapter;


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

    private  void onClickLoadIsbn(){
        String text = mTextIsbn.getText().toString();
        if(text.length() == 0){
            builder.setMessage("NO input");
            builder.create().show();
            return;
        }


        setStartLoadView();
        myIsbnService = new IsbnService(text);
        myIsbnService.setListener(new IServiceComplete() {
            @Override
            public void callback(String Json) {
                //Prüfen ob Error oder nicht
                if(myIsbnService.isError(Json)){
                    mTextError.setText(myIsbnService.getErrorText(Json));
                    setViewErrorIsbn();
                }else {
                    changeIsbnDataList(myIsbnService.getIsbnDataList(Json));
                    setViewOkIsbn();
                }
                setStopLoadView();
            }
        });
        myIsbnService.execute();
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
        adapter = new ArrayAdapter<String[]>(this,android.R.layout.simple_list_item_2, android.R.id.text1,isbnData){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                String[] entry = isbnData.get(position);
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(entry[0]);
                text2.setText(entry[1]);
                text1.setAllCaps(true);
                return view;
            }
        };
        mListView.setAdapter(adapter);
    }

    private void changeIsbnDataList(ArrayList<String[]> list){
        for(int i = 0; i < list.size(); i ++){
            isbnData.add(list.get(i));
        }
        adapter.notifyDataSetChanged();
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
