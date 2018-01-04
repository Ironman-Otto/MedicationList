package data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lsdinfotech.medicationlist.R;

import java.util.ArrayList;

import model.FamilyMember;

/**
 * Created by Ironman on 4/18/2017.
 * This is the custom ArrayAdapter for the family list
 */

public class FamilyMemberListArrayAdapter extends ArrayAdapter<FamilyMember> {

    private final Context context;
    private final ArrayList<FamilyMember> familyMembers;
    private ViewHolder viewHolder = null;
    private int resource;


    public FamilyMemberListArrayAdapter( Context context, int resource, ArrayList<FamilyMember> familyMembers) {
        super(context, resource, familyMembers);
        this.context = context;
        this.resource = resource;
        this.familyMembers = familyMembers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View familyView = convertView;

        if (familyView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            familyView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)familyView.findViewById(R.id.family_member_list_name);
            viewHolder.bday = (TextView)familyView.findViewById(R.id.family_member_list_bday);
            viewHolder.gender = (TextView)familyView.findViewById(R.id.family_member_list_gender);
            familyView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)familyView.getTag();
        }

        populateDetails(position);

        return familyView;
    }

    /**
     * populateDetails
     * This method populated the details for the listview
     */
    private void populateDetails(int position) {

        viewHolder.name.setText(familyMembers.get(position).toString());
        viewHolder.bday.setText(familyMembers.get(position).getBirthdate());
        viewHolder.gender.setText(familyMembers.get(position).getGender());
    }

    /**
     * ViewHolder
     * This class holds all the views contained in the layout which will
     * be populated
     */
    private class ViewHolder {

        TextView name;
        TextView bday;
        TextView gender;
    }
}
