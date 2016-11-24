package com.algonquincollege.king0410.doorsopenottawa;

/**
 *  Alex King
 *  king0410
 */

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.algonquincollege.king0410.doorsopenottawa.parsers.BuildingJSONparser;
import com.algonquincollege.king0410.doorsopenottawa.Model.Building;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;

    // private AboutDialogFragment mAboutDialog;

    private static final String ABOUT_DIALOG_TAG;
    private static final String LOG_TAG;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
        LOG_TAG = "Doors Open Ottawa";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
        requestData(REST_URI);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building selectedBuilding = buildingList.get(position);
                Intent intent = new Intent( getApplicationContext(), Description.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "name",  selectedBuilding.getName());
                intent.putExtra( "description", selectedBuilding.getDescription() );
                intent.putExtra( "address", selectedBuilding.getAddress() );
                startActivity( intent );
            }
        });
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            //return content;
            buildingList = BuildingJSONparser.parseFeed(content);


            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();
        }
    }
}