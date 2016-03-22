package hr.from.bkoruznjak.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import hr.from.bkoruznjak.top10downloader.controllers.ParseApplications;
import hr.from.bkoruznjak.top10downloader.model.Application;

public class MainActivity extends AppCompatActivity {

    private String mFileContents;
    private Button btnParseXml;
    private ListView listApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnParseXml = (Button) findViewById(R.id.btnParseXml);
        listApps = (ListView) findViewById(R.id.xmlListView);

        btnParseXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DownloadData", "Button parse clicked!");
                //TODO: Add parse activation code
                ParseApplications parseApplications = new ParseApplications(mFileContents);
                parseApplications.process();
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainActivity.this, R.layout.list_item, parseApplications.getApplications());
                listApps.setAdapter(arrayAdapter);
            }
        });

//       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
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

    private class DownloadData extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {

            mFileContents = downloadXMLFile(params[0]);
            if (mFileContents == null) {
                Log.d("DownloadData" , "Error downloading");
            }

            return mFileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("DownloadData", "Result was: " + result);
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder tempBuffer = new StringBuilder();
            try{
                //setup url
                URL url = new URL(urlPath);
                //opens connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("DownloadData" , "The response code was " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isa = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];
                while(true){
                    charRead = isa.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }
                    tempBuffer.append(String.valueOf(inputBuffer, 0, charRead));
                }
                return tempBuffer.toString();
            } catch (IOException | SecurityException e){
                Log.d("DownloadData" ,"Exception reading data: " + e.getMessage());
            }
            return null;
        }
    }
}
