package data;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lsdinfotech.medicationlist.R;

import java.util.ArrayList;
import model.MyEvent;


/**
 * Created by Ironman on 5/29/2017.
 * @author Otto L. Lecuona
 * This is the custom array adapter for the reminder list
 */

public class ReminderListArrayAdapter extends ArrayAdapter<MyEvent> {
    final private Context context;
    final private ArrayList<MyEvent> events;
    private int resource;
    private ViewHolder viewHolder;
    private static final String TIME_FORMAT = "hh:mm aa";
    private static final String DATE_FORMAT = "MM-dd-yyyy";

    public ReminderListArrayAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<MyEvent> events) {
        super(context, resource, events);
        this.context = context;
        this.events = events;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View layout = convertView;


        if (layout == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) layout.findViewById(R.id.reminder_list_detail_time);
            viewHolder.name = (TextView) layout.findViewById(R.id.reminder_list_detail_name);
            viewHolder.onDays = (TextView) layout.findViewById(R.id.reminder_list_detail_occurance_byday);

            layout.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) layout.getTag();
        }

        processEvent(position);

        return layout;
    }

    /**
     * processEvent
     * This method prepares the view to be presented on the list
     * @param position within event list
     */
    private void processEvent(int position) {
        viewHolder.name.setText(events.get(position).getEventTitle());
        viewHolder.time.setText(events.get(position).calculateEventTime(TIME_FORMAT));

        if (events.get(position).isRecurring()) {
            viewHolder.onDays.setText(events.get(position).generateOccursOn());
        } else {
            // Specific date
            viewHolder.onDays.setText(events.get(position).calculateEventTime(DATE_FORMAT));

        }

    }

    /**
     * Class to hold all the views for the details
     */
    private class ViewHolder{
        TextView time;
        TextView name;
        TextView onDays;
    }

}
