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

import model.Medication;
import utility.StringUtil;

/**
 * Created by Ironman on 4/18/2017.
 * This is the custom ArrayAdapter for the family list
 */

public class MedicationListArrayAdapter extends ArrayAdapter<Medication> {

    private final Context                   context;
    private final ArrayList<Medication>     medications;
    private ViewHolder                      viewHolder = null;
    private int                             resource;


    public MedicationListArrayAdapter(Context context, int resource, ArrayList<Medication> medications) {
        super(context, resource, medications);
        this.context = context;
        this.resource = resource;
        this.medications = medications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View medicationView = convertView;

        if (medicationView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            medicationView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.medicationName = (TextView)medicationView.findViewById(R.id.medication_list_detail_name);
            viewHolder.medicationDosage = (TextView)medicationView.findViewById(R.id.medication_list_detail_dosage);
            viewHolder.medicationQuantity = (TextView)medicationView.findViewById(R.id.medication_list_detail_quantity);
            viewHolder.medicationFamilyMember = (TextView)medicationView.findViewById(R.id.medication_list_detail_family_member);
            viewHolder.medicationPhysician = (TextView)medicationView.findViewById(R.id.medication_list_detail_physician);
            viewHolder.medicationBrandName = (TextView)medicationView.findViewById(R.id.medication_list_detail_brand_name);
            viewHolder.medicationTakeInstr = (TextView)medicationView.findViewById(R.id.medication_list_detail_instruction);
            medicationView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)medicationView.getTag();
        }

        populateDetails(position);

        return medicationView;
    }

    /**
     * populateDetails
     * This method populated the details for the listview
     */
    private void populateDetails(int position) {
        String quantity;

        viewHolder.medicationName.setText(medications.get(position).getName());
        viewHolder.medicationBrandName.setText(medications.get(position).getBrandName());
        viewHolder.medicationDosage.setText(medications.get(position).getStrength());

        if(medications.get(position).getFamilyMemberPid() > 0) {
            viewHolder.medicationFamilyMember.setText(Database.familyTable
                    .queryFamilyMembers(false, medications.get(position).getFamilyMemberPid(), null).get(0).toString());
        } else {
            viewHolder.medicationFamilyMember.setText("");
        }

        if(medications.get(position).getPhysicianPid() > 0) {
            viewHolder.medicationPhysician.setText(Database.physicianTable
                    .queryPhysicians(false, medications.get(position).getPhysicianPid(), null).get(0).toString());
        } else {
            viewHolder.medicationPhysician.setText("");
        }

        if (StringUtil.isNullEmptyBlank(medications.get(position).getQuantity())){
            quantity = context.getString(R.string.not_specified);
        }else{
            quantity = medications.get(position).getQuantity();
        }
        viewHolder.medicationQuantity.setText(quantity);
        viewHolder.medicationTakeInstr.setText(medications.get(position).getTakeInstruct());

    }

    /**
     * ViewHolder
     * This class holds all the views contained in the layout which will
     * be populated
     */
    private class ViewHolder {

        TextView medicationName;
        TextView medicationBrandName;
        TextView medicationDosage;
        TextView medicationQuantity;
        TextView medicationFamilyMember;
        TextView medicationPhysician;
        TextView medicationTakeInstr;

    }

}
