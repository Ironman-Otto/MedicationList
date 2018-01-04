package com.lsdinfotech.medicationlist;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import data.Constants;
import data.Database;
import data.MedicationListArrayAdapter;
import data.MedsDb;
import model.Medication;

public class MedicationListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView                    medicationListView;
    private ArrayList<Medication>       medicationArrayList;
    private ArrayAdapter<Medication>    arrayAdapter;
    private int                         primaryKeySort;
    private int                         sortType = Constants.SORT_TYPE_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
        setUpEventHandlers();
        setUpArrayAdapater();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MedicationActivity.class);
                intent.putExtra( Constants.MODE, Constants.INSERT);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ListAsyncTask().execute(setParams(primaryKeySort, sortType));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medication_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = new Intent();
        int request = Constants.REQUEST_PRIMARY_KEY;

        switch (id) {
            case R.id.action_sort_by_family_member:{
                intent.setClass(getApplicationContext(), FamilyMemberListActivity.class);
                intent.putExtra(Constants.MODE, Constants.PRIMARY_KEY_REQUESTED);
                break;
            }
            case R.id.action_sort_by_physician:{
                intent.setClass(getApplicationContext(), PhysicianListActivity.class);
                intent.putExtra(Constants.MODE, Constants.PRIMARY_KEY_REQUESTED);
                break;
            }
            case R.id.action_sort_all:{
                sortType = Constants.SORT_TYPE_ALL;
                MedicationListActivity.this.onResume();
                return true;
            }
        }

        startActivityForResult(intent, request);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), MedicationActivity.class);
        intent.putExtra( Constants.MODE, Constants.UPDATE );
        intent.putExtra( Constants.PRIMARY_KEY, medicationArrayList.get(position).getPrimaryKey() );
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case Constants.REQUEST_PRIMARY_KEY:{
                    primaryKeySort = data.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_PRIMARY_KEY);
                    sortType = data.getIntExtra(Constants.NAME_TYPE, Constants.NO_NAME_TYPE);
                    break;

                }
                case Constants.REQUEST_DELETE:{
                    int foriegnKeyDelete = data.getIntExtra(Constants.PRIMARY_KEY, Constants.NO_NAME_TYPE);
                    updateMedicationForeignKey(foriegnKeyDelete, data.getIntExtra(Constants.NAME_TYPE, Constants.NO_NAME_TYPE));
                    break;
                }

            }
        }
    }

    /**
     * updateMedicationForiegnKey
     * This method updates the foreign key fields in the medication table
     * based on the type of foreign key that is passed in and value of key
     * @param foreignKeyDelete the key to delete
     * @param type family member or physician
     */
    private void updateMedicationForeignKey(int foreignKeyDelete, int type) {
        ContentValues cv = new ContentValues();
        String whereClause;
        if ( foreignKeyDelete > 0 ) {

            switch (type) {
                case Constants.NAME_FAMILY_MEMBER: {
                    cv.put(MedsDb.FAMILY_MEMBER_PID, 0);
                    cv.put(MedsDb.FAMILY_MEMBER_NAME, "");
                    whereClause = MedsDb.FAMILY_MEMBER_PID + " = " + foreignKeyDelete;
                    break;
                }
                case Constants.NAME_PHYSICIAN: {
                    cv.put(MedsDb.PHYSICIAN_PID, 0);
                    cv.put(MedsDb.PHYSICIAN_NAME, "");
                    whereClause = MedsDb.PHYSICIAN_PID + " = " + foreignKeyDelete;
                    break;
                }
                default: {
                    return;
                }
            }

            Database.medTable.update(cv, whereClause);
        }

    }
    /**
     * setParams
     * This method sets the param where clause for the Medication list
     * @param primaryKeySort value of the primary key to sort with
     * @param sortType Family or Physician
     * @return String[] with where clause set
     */
    private String[] setParams(int primaryKeySort, int sortType) {
        String[] params = {"", "", ""};

        if ( sortType == Constants.SORT_TYPE_FAMILY_MEMBER && primaryKeySort > 0){
            params[2] = MedsDb.FAMILY_MEMBER_PID + " = " + primaryKeySort;
        } else if (sortType == Constants.SORT_TYPE_PHYSICIAN && primaryKeySort > 0) {
            params[2] = MedsDb.PHYSICIAN_PID + " = " + primaryKeySort;
        } else {
            params[2] = null;
        }

        return params;
    }

    /**
     * setUpViews
     * This method sets up the views for the family member list
     */
    private void setUpViews() {
        medicationListView = (ListView)findViewById(R.id.medication_listview);
    }

    /**
     * setUpEventHandlers
     * This method registers the event handlers used by this activity
     */
    private void setUpEventHandlers() {
        medicationListView.setOnItemClickListener(this);
    }

    /**
     * setUpArrayAdapter
     * This method prepares the array adapter for listing the medications.
     */
    private void setUpArrayAdapater() {
        medicationArrayList = new ArrayList<>();
        arrayAdapter = new MedicationListArrayAdapter(this, R.layout.medication_list_detail, medicationArrayList);
        medicationListView.setAdapter(arrayAdapter);
    }

    /**
     * This class is used to access the database in the background.
     */
    private class ListAsyncTask extends AsyncTask<String, Integer, ArrayList<Medication>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            medicationArrayList.clear();
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<Medication> medications) {
            super.onPostExecute(medications);

            if (medications != null) {
                Collections.sort(medications, new Comparator<Medication>() {
                    @Override
                    public int compare(Medication o1, Medication o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                for (int i = 0; i < medications.size(); i++) {
                    medicationArrayList.add(medications.get(i));
                    arrayAdapter.notifyDataSetChanged();

                }
            }
        }

        @Override
        protected ArrayList<Medication> doInBackground(String... params) {

            return Database.medTable.queryMedications(false, 0, params[2]);
        }
    }
}
