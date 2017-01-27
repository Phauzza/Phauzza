package com.pahuza.pahuza.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pahuza.pahuza.R;
import com.pahuza.pahuza.models.Job;

import java.util.ArrayList;

/**
 * Created by baryariv on 22/01/2017.
 */
public class JobsAdapter extends ArrayAdapter<Job> {
    private final Context context;
    private final ArrayList<Job> data;

    // View lookup cache
    private static class ViewHolder {
        TextView descTxt;
        TextView doneTxt;
    }

    public JobsAdapter(ArrayList<Job> data, Context context) {
        super(context, R.layout.jobs_item, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Job job = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.jobs_item, parent, false);
            viewHolder.descTxt = (TextView) convertView.findViewById(R.id.desc_txt);
            viewHolder.doneTxt = (TextView) convertView.findViewById(R.id.done_txt);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.descTxt.setText("Description: " + job.getDescription());
        if(job.getStatus().equals("0"))
            viewHolder.doneTxt.setText("Photo Taken: X");
        else
            viewHolder.doneTxt.setText("Photo Taken: V");


        // Return the completed view to render on screen
        return convertView;
    }
}

