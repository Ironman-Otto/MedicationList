package com.lsdinfotech.medicationlist;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import data.Constants;
import data.Database;
import data.PhysicianListArrayAdapter;
import model.Name;
import model.Physician;

public class PhysicianListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView                    physicianListView;
    private ArrayList<Physician>        physicianArrayList;
    private ArrayAdapter<Physician>     arrayAdapter;
    private int                         mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physician_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
        setUpEventHandlers();
        setUpArrayAdapater();

        Intent intent   = getIntent();
        mode            = intent.getIntExtra(Constants.MODE, Constants.NO_REQUEST);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_physician_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_add:{
                processAddAction();
                break;
            }
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        String[] params = {"","", null};
        new ListAsyncTask().execute(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.NAME_REQUEST: {
                   determinineNameRequestResponse(mode, data);
                    break;

                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (mode) {
            case Constants.NAME_REQUESTED:{
                setResult(RESULT_OK, returnNameOnClick(position));
                finish();
                break;
            }
            case Constants.PRIMARY_KEY_REQUESTED: {
                setResult(RESULT_OK, returnPrimaryKey(position));
                finish();
                break;
            }
            case Constants.DELETE_REQUESTED:{
                deleteSelectedPhysician(position);
                setResult(RESULT_OK, returnPrimaryKey(position));
                finish();
                break;
            }
            default:{
                startActivity(updatePhysician(position));
                break;
            }

        }



    }

    /**
     * processAddAction
     * This method processes the add action on toolbar
     * It will request a new physician name entry if it is not
     */
    private void processAddAction() {
        if (mode != Constants.PRIMARY_KEY_REQUESTED && mode != Constants.DELETE_REQUESTED) {

            Intent intent = new Intent(getApplicationContext(), NameActivity.class);
            intent.putExtra(Constants.NAME_TYPE, Constants.NAME_PHYSICIAN);
            startActivityForResult(intent, Constants.NAME_REQUEST);
        }
    }

    /**
     * deleteSelectedPhysician
     * This method deletes the selected physician from the list
     * @param position selected member
     */
    private void deleteSelectedPhysician(int position) {
        int rc = physicianArrayList.get(position).getPrimaryKey();
        Database.physicianTable.delete(rc);
    }
    /**
     * prepareReturnIntent
     * This method prepares the intent that will be returned on a NAME_REQUEST
     * @param data intent data recieved from NAME_REQUEST
     * @return Intent
     */
    private Intent returnNameRequest(Intent data) {
        Intent intent = new Intent();
        String jsonName = data.getStringExtra(Constants.RETURNED_NAME);
        int primaryKey = data.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY);

        intent.putExtra(Constants.RETURNED_NAME, jsonName);
        intent.putExtra(Constants.NAME_TYPE, Constants.NAME_PHYSICIAN);
        intent.putExtra(Constants.PRIMARY_KEY, primaryKey);

        return intent;

    }

    /**
     * returnNameOnClick
     * This method returns the name and primary key of the selected physiciam from
     * the list. This is a response to a NAME REQUEST using existing physician
     * @param position selected physician
     * @return Intent with return data
     */
    private Intent returnNameOnClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.NAME_TYPE, Constants.NAME_PHYSICIAN);
        intent.putExtra(Constants.RETURNED_NAME, prepareJsonString(position));
        intent.putExtra(Constants.PRIMARY_KEY, physicianArrayList.get(position).getPrimaryKey());

        return intent;
    }

    /**
     * returnPrimaryKey
     * This method returns the proper data depending on the primary key request
     * When the primary key is requested then we send back the key of the selected item
     * @param position selected physician from list
     * @return Intent
     */
    private Intent returnPrimaryKey(int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.NAME_TYPE, Constants.NAME_PHYSICIAN);
        intent.putExtra(Constants.PRIMARY_KEY, physicianArrayList.get(position).getPrimaryKey());
        return intent;
    }

    /**
     * updatePhysician
     * This method is used to update the selected physician
     * @param position physician to update
     * @return Intent with mode
     */
    private Intent updatePhysician(int position) {
        Intent intent = new Intent(getApplicationContext(), PhysicianActivity.class);
        intent.putExtra(Constants.MODE, Constants.UPDATE);
        intent.putExtra(Constants.PRIMARY_KEY, physicianArrayList.get(position).getPrimaryKey());
        return intent;
    }

    /**
     * setUpViews
     * This method sets up the views for the physician list
     */
    private void setUpViews() {
        physicianListView = (ListView)findViewById(R.id.physician_listview);
    }

    /**
     * setUpEventHandlers
     * This method registers the event handlers used by this activity
     */
    private void setUpEventHandlers() {
        physicianListView.setOnItemClickListener(this);
    }

    /**
     * setUpArrayAdapter
     * This method prepares the array adapter for listing the family members.
     */
    private void setUpArrayAdapater() {
        physicianArrayList = new ArrayList<>();
        arrayAdapter = new PhysicianListArrayAdapter(this, R.layout.physician_list_detail, physicianArrayList);
        physicianListView.setAdapter(arrayAdapter);
    }

    /**
     * This class is used to access the database in the background.
     */
    private class ListAsyncTask extends AsyncTask<String, Integer, ArrayList<Physician>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            physicianArrayList.clear();
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<Physician> physicians) {
            super.onPostExecute(physicians);

            if (physicians != null) {
                Collections.sort(physicians, new Comparator<Physician>() {
                    @Override
                    public int compare(Physician o1, Physician o2) {
                        return o1.getLastName().compareTo(o2.getLastName());
                    }
                });

                for (int i = 0; i < physicians.size(); i++) {
                    physicianArrayList.add(physicians.get(i));
                    arrayAdapter.notifyDataSetChanged();

                }
            }
        }

        @Override
        protected ArrayList<Physician> doInBackground(String... params) {

            return Database.physicianTable.queryPhysicians(true, 0, params[2]);
        }
    }

    /**
     * prepareJsonString
     * This method reads the name information from the selected item from the list.
     * Creates and populates a Name structure then converts this to a Json string
     * @param position selection from list
     * @return json string
     */
    private String prepareJsonString(int position) {
        Name name = new Name();
        name.setNameSuffix(physicianArrayList.get(position).getSuffix());
        name.setFirstName(physicianArrayList.get(position).getFirstName());
        name.setMiddleName(physicianArrayList.get(position).getMiddleName());
        name.setLastName(physicianArrayList.get(position).getLastName());
        name.setNamePrefix(physicianArrayList.get(position).getPrefix());

        Gson gson = new Gson();

        return gson.toJson(name);

    }

    /**
     * determineRequestResponse
     * This method determines what response to take depending on mode
     * If the mode is NO_REQUEST that means it was not initiated from outside
     * the program but rather from itself and should remain in active state
     * @param mode current mode of activity
     */
    private void determinineNameRequestResponse(int mode, Intent data) {
        switch (mode) {
            case Constants.NO_REQUEST:
                break;
            default:{
                setResult(RESULT_OK, returnNameRequest(data));
                finish();
            }
        }

    }
}
