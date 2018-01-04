package utility;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ironman on 4/13/2017.
 * @author Otto L. Lecuona
 * This class is used to setup and control a spinner
 *
 * my_spinner_item
 * This is a custom layout for spinner item
    *<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    *android:id="@android:id/text1"
    *android:layout_width="match_parent"
    *android:layout_height="wrap_content"
    *android:padding="@dimen/textview_padding"
    *android:textSize="@dimen/edittext_textsize"
    *android:textColor="@color/colorText" />
 *
 * my_spinner_dropdown_item
 * This is a custom layout for spinner dropdown item
 * <CheckedTextView xmlns:android="http://schemas.android.com/apk/res/android"
    *android:id="@android:id/text1"
    *style="?android:attr/spinnerDropDownItemStyle"
    *android:maxLines="1"
    *android:checkMark="?android:attr/listChoiceIndicatorMultiple"
    *android:layout_width="match_parent"
    *android:layout_height="?android:attr/listPreferredItemHeightSmall"
    *android:ellipsize="marquee"
    *android:textColor="@color/colorText"/>
 *
 * in apptheme in styles
 * <item name="android:dropDownListViewStyle">@style/App.Style.Spinner</item>
 *
 * in styles
    *<style name="App.Style.Spinner" parent="@style/Widget.AppCompat.ListView.DropDown">
    *<item name="android:dividerHeight">2dp</item>
    *<item name="android:divider">@drawable/divider_line_2</item>
    *</style>
 */

public class SpinnerUtil {

    private String selection;
    private Spinner spinner;
    private String[] spinnerList;
    private int arrayAdapterLayout;
    private int dropDownLayout;
    private Context context;
    private int selectionPosition;
    private int SUCCESS = 0;

    /**
     * SpinnerUtil
     * This is the constructor for the spinner. All related items associated with a spinner are
     * set by the constructor.
     * @param context Context
     * @param spinner Spinner
     * @param spinnerList String[]
     * @param arrayAdapterLayout int
     * @param dropDownLayout int
     */
    public SpinnerUtil(Context context, Spinner spinner, String[] spinnerList, int arrayAdapterLayout, int dropDownLayout) {
        this.spinner = spinner;
        this.spinnerList = spinnerList;
        this.arrayAdapterLayout = arrayAdapterLayout;
        this.dropDownLayout = dropDownLayout;
        this.context = context;
    }

    /**
     * getSelection
     * This method returns the selection that has been made from the spinner
     * @return selection String
     */
    public String getSelection() {
        return selection;
    }

    /**
     * setSelection
     * This method is used to set the value of the selection to be set as the
     * current selection to be displayed. It calls the findPosition method to
     * set the index of the selection
     * @param selection String
     */
    public void setSelection(String selection) {
        this.selection = selection;

        if (findPositionOfSelection() == SUCCESS) {
            spinner.setSelection(selectionPosition);
        }
    }

    /**
     * resetSpinner
     * This method resets the spinner the spinner to the first position
     * on the spinner list
     */
    public void resetSpinner() {
        spinner.setSelection(0);
    }

    /**
     * setUpSpinner
     * This method will load the list of spinner items from the string array spinnerList. The method
     * then sorts the list. The ArrayAdapter for the spinner is created with layouts set. The adapter
     * is then set for the spinner.
     */
    public void setUpSpinner(boolean sort) {

        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, spinnerList);
        if ( sort ) {
            Collections.sort(list);
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, arrayAdapterLayout, list);
        dataAdapter.setDropDownViewResource(dropDownLayout);
        spinner.setAdapter(dataAdapter);

        setListener();

    }

    /**
     * setListener
     * This method sets the OnItemSelectedListener for the spinner. Once a selection is made
     * the choice is set as the current selection.
     */
    private void setListener() {

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * findPositionOfSelection
     * This method searches the spinner list to find the position of the
     * current selection.
     * @return 0 for success, -1 for selection not found
     */
    private int findPositionOfSelection() {

        for ( int i = 0; i < spinnerList.length ; i++ ) {

            if ( spinnerList[i].equals(selection)){
                selectionPosition = i;
                return SUCCESS;
            }

        }

        return 1;

    }

}

