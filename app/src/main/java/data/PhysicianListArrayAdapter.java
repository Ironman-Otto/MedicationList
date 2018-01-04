package data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lsdinfotech.medicationlist.R;

import java.util.ArrayList;

import model.Physician;
import utility.PhoneUtil;
import utility.StringUtil;

/**
 * Created by Ironman on 4/18/2017.
 * This is the custom ArrayAdapter for the physician list
 *
 * Note: When building a viewholder it is important to fill in all elements of the
 * viewholder each time through or there can be residual effects. A previous setting
 * may carry over as was the case with the phonelayout onclicklistener. It was being set only
 * when a phone number was present. However, the view holder remembered the last onclicklistener
 * and associated it with the  first position in  the list. This was corrected by setting
 * the onclicklistener to null when not set.
 */

public class PhysicianListArrayAdapter extends ArrayAdapter<Physician> {

    private final Context               context;
    private final ArrayList<Physician>  physicians;
    private ViewHolder                  viewHolder = null;
    private int                         resource;


    public PhysicianListArrayAdapter(Context context, int resource, ArrayList<Physician> physicians) {
        super(context, resource, physicians);
        this.context = context;
        this.resource = resource;
        this.physicians = physicians;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View physicianView = convertView;

        if (physicianView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            physicianView = inflater.inflate(resource, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) physicianView.findViewById(R.id.physician_list_name);
            viewHolder.phoneNumber = (TextView) physicianView.findViewById(R.id.physician_list_phone);
            viewHolder.phoneCallBtn = (ImageView) physicianView.findViewById(R.id.physician_list_phone_btn);
            viewHolder.specialty = (TextView) physicianView.findViewById(R.id.physician_list_specialty);
            viewHolder.phoneLayout = (LinearLayout) physicianView.findViewById(R.id.physician_list_phone_layout);

            physicianView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) physicianView.getTag();
        }

        populateDetails(position);

        return physicianView;
    }

    /**
     * populateDetails
     * This method populated the details for the listview
     */
    private void populateDetails(int position) {

        viewHolder.name.setText(physicians.get(position).toString());

        viewHolder.specialty.setVisibility(View.VISIBLE);
        if (StringUtil.isNotNullEmptyBlank(physicians.get(position).getSpecialty())) {
            viewHolder.specialty.setText(physicians.get(position).getSpecialty());
        } else {
            viewHolder.specialty.setVisibility(View.GONE);
        }

        String phoneNumber = physicians.get(position).getPhone();
        if (StringUtil.isNotNullEmptyBlank(phoneNumber)) {
            String phone = PhoneUtil.formatPhoneNumber(context, phoneNumber);
            viewHolder.phoneNumber.setText(phone);
            phoneNumber = "tel:" + phoneNumber; // tel: must preceed the phonenumber
            setPhoneBtnOnClickListner(viewHolder.phoneLayout, phoneNumber, context);
        } else {
            viewHolder.phoneNumber.setText(R.string.no_phone);
            viewHolder.phoneLayout.setOnClickListener(null);
        }
    }

    /**
     * setPhoneBtnOnClickListner
     * This method sets up a handler for the phone number button if there is a phone
     * number on file
     * @param phoneBtn imageview to initiate call
     * @param phoneNumber number to call
     * @param context application context
     */
    private void setPhoneBtnOnClickListner(LinearLayout phoneBtn, final String phoneNumber, final Context context) {
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtil.confirmCallRequest(phoneNumber, context);
            }
        });
    }
    /**
     * ViewHolder
     * This class holds all the views contained in the layout which will
     * be populated
     */
    private class ViewHolder {

        TextView name;
        TextView phoneNumber;
        ImageView phoneCallBtn;
        TextView specialty;
        LinearLayout phoneLayout;
    }

}
