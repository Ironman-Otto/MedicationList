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
import data.FamilyMemberListArrayAdapter;
import model.FamilyMember;
import model.Name;

public class FamilyMemberListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView                    familyMemberListView;
    private ArrayList<FamilyMember>     familyMembersArrayList;
    private ArrayAdapter<FamilyMember>  arrayAdapter;
    private int                         mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
        setUpEventHandlers();
        setUpArrayAdapater();

        Intent intent = getIntent();
        mode = intent.getIntExtra(Constants.MODE, Constants.NO_REQUEST);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_family_member_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();

        switch (id) {
            case R.id.action_new:
                processAddAction();
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] params = {"","",null};
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
                deleteSelectedFamilyMember(position);
                setResult(RESULT_OK, returnPrimaryKey(position));
                finish();
                break;
            }
            default:{
                startActivity(updateFamilyMember(position));
                break;
            }
        }

    }
    /**
     * processAddAction
     * This method processes the add action on toolbar
     * It will request a new family member name entry if it is not
     */
    private void processAddAction() {
        if (mode != Constants.PRIMARY_KEY_REQUESTED && mode != Constants.DELETE_REQUESTED) {

            Intent intent = new Intent(getApplicationContext(), NameActivity.class);
            intent.putExtra(Constants.NAME_TYPE, Constants.NAME_FAMILY_MEMBER);
            startActivityForResult(intent, Constants.NAME_REQUEST);
        }
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

    /**
     * updateFamilyMember
     * This method is used to update the selected family member
     * @param position family member to update
     * @return Intent with mode
     */
    private Intent updateFamilyMember(int position) {
        Intent intent = new Intent(getApplicationContext(), FamilyMemberActivity.class);
        intent.putExtra(Constants.MODE, Constants.UPDATE);
        intent.putExtra(Constants.PRIMARY_KEY, familyMembersArrayList.get(position).getPrimaryKey());
        return intent;
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
        intent.putExtra(Constants.NAME_TYPE, Constants.NAME_FAMILY_MEMBER);
        intent.putExtra(Constants.PRIMARY_KEY, primaryKey);

        return intent;

    }

    /**
     * setUpViews
     * This method sets up the views for the family member list
     */
    private void setUpViews() {
        familyMemberListView = (ListView)findViewById(R.id.family_member_listview);
    }

    /**
     * setUpEventHandlers
     * This method registers the event handlers used by this activity
     */
    private void setUpEventHandlers() {
        familyMemberListView.setOnItemClickListener(this);
    }

    /**
     * setUpArrayAdapter
     * This method prepares the array adapter for listing the family members.
     */
    private void setUpArrayAdapater() {
        familyMembersArrayList = new ArrayList<>();
        arrayAdapter = new FamilyMemberListArrayAdapter(this, R.layout.family_member_list_detail, familyMembersArrayList);
        familyMemberListView.setAdapter(arrayAdapter);
    }

    /**
     * This class is used to access the database in the background.
     */
    private class ListAsyncTask extends AsyncTask<String, Integer, ArrayList<FamilyMember>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            familyMembersArrayList.clear();
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<FamilyMember> familyMembers) {
            super.onPostExecute(familyMembers);

            if (familyMembers != null) {
                Collections.sort(familyMembers, new Comparator<FamilyMember>() {
                    @Override
                    public int compare(FamilyMember o1, FamilyMember o2) {
                        return o1.toString().compareTo(o2.toString());
                    }
                });
                for (int i = 0; i < familyMembers.size(); i++) {
                    familyMembersArrayList.add(familyMembers.get(i));
                    arrayAdapter.notifyDataSetChanged();

                }
            }
        }

        @Override
        protected ArrayList<FamilyMember> doInBackground(String... params) {

            return Database.familyTable.queryFamilyMembers(true, 0, params[2]);
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
        name.setNameSuffix(familyMembersArrayList.get(position).getSuffix());
        name.setFirstName(familyMembersArrayList.get(position).getFirstName());
        name.setMiddleName(familyMembersArrayList.get(position).getMiddleName());
        name.setLastName(familyMembersArrayList.get(position).getLastName());
        name.setNamePrefix(familyMembersArrayList.get(position).getPrefix());

        Gson gson = new Gson();

        return gson.toJson(name);

    }

    /**
     * returnPrimaryKey
     * This method returns the proper data depending on the primary key request
     * When the primary key is requested then we send back the key of the selected item
     * @param position selected family member from list
     * @return Intent
     */
    private Intent returnPrimaryKey(int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.NAME_TYPE, Constants.NAME_FAMILY_MEMBER);
        intent.putExtra(Constants.PRIMARY_KEY, familyMembersArrayList.get(position).getPrimaryKey());
        return intent;
    }

    /**
     * deleteSelectedFamilyMember
     * This method deletes the selected family member from the family member list
     * @param position selected member
     */
    private void deleteSelectedFamilyMember(int position) {
        int rc = familyMembersArrayList.get(position).getPrimaryKey();
        Database.familyTable.delete(rc);
    }

    /**
     * returnNameOnClick
     * This method returns the name and primary key of the selected family member from
     * the list. This is a response to a NAME REQUEST using existing family member
     * @param position selected family member
     * @return Intent with return data
     */
    private Intent returnNameOnClick(int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.NAME_TYPE, Constants.NAME_FAMILY_MEMBER);
        intent.putExtra(Constants.RETURNED_NAME, prepareJsonString(position));
        intent.putExtra(Constants.PRIMARY_KEY, familyMembersArrayList.get(position).getPrimaryKey());

        return intent;
    }
}
