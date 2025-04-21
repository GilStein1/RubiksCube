package com.example.my3dproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ScoresAdapter extends ArrayAdapter<Account> {

    private final Context context;
    private final List<Account> accountsList;

    public ScoresAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Account> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.accountsList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();

        View v = layoutInflater.inflate(R.layout.stats_list, parent, false);

        Account currentAccount = accountsList.get(position);

        TextView tvPlace = v.findViewById(R.id.tvPlace);
        TextView tvUsername = v.findViewById(R.id.tvUsername);
        TextView tvBestTime = v.findViewById(R.id.tvBestTime);

        int place = (accountsList.indexOf(currentAccount) + 1);

        tvPlace.setText(place + (place == 1 ? "st" : (place == 2) ? "nd" : (place == 3) ? "rd" : "th") + " place:");
        tvUsername.setText(currentAccount.getName());
        String minutes = (int)(currentAccount.getBestTime()/60) < 10 ? "0" + (int)(currentAccount.getBestTime()/60) : "" + (int)(currentAccount.getBestTime()/60);
        String seconds = (int)(currentAccount.getBestTime() % 60) < 10 ? "0" + (int)(currentAccount.getBestTime() % 60) : "" + (int)(currentAccount.getBestTime() % 60);
        if(currentAccount.getBestTime() == Double.MAX_VALUE) {
            minutes = "??";
            seconds = "??";
        }
        tvBestTime.setText("Best Time Is: ⏱ " + minutes + ":" + seconds);

        return v;
    }

}
