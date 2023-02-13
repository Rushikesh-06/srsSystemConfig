package com.emi.srsSystemConfiguration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class InfoAdapter extends ArrayAdapter<InfoModel> {

    public InfoAdapter(@NonNull Context context, ArrayList<InfoModel> itemModelArrayList) {
        super(context, 0, itemModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }
        InfoModel infoModel = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
        ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);
        TextView courseTV2 = listitemView.findViewById(R.id.idTVCourse2);

        courseTV.setText(infoModel.getItem_name());
        courseIV.setImageResource(infoModel.getImgid());
        courseTV2.setText(infoModel.getTitle_name());
        return listitemView;
    }
}