package com.example.my3dproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * The adapter for the ListView in the stats activity
 */
public class ScoresAdapter extends ArrayAdapter<Account> {

    // The context
    private final Context context;
    // A list containing all the created accounts
    private final List<Account> accountsList;

    /**
     * The constructor that create an instance of the adapter
     */
    public ScoresAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Account> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.accountsList = objects;
    }

    /**
     * A method that os called for every item in the ListView.
     * It sets the profile picture, username and best score.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Creating the item on the list
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.stats_list, parent, false);

        // Getting the connected account
        Account currentAccount = accountsList.get(position);

        // Initializing all views
        ImageView ivProfilePicture = v.findViewById(R.id.ivProfilePicture);
        TextView tvPlace = v.findViewById(R.id.tvPlace);
        TextView tvUsername = v.findViewById(R.id.tvUsername);
        TextView tvBestTime = v.findViewById(R.id.tvBestTime);

        // Setting the profile picture from the encoded String
        if(!currentAccount.getProfilePicture().isEmpty()) {
            byte[] decodedBytes = Base64.decode(currentAccount.getProfilePicture(), Base64.DEFAULT);
            ivProfilePicture.setImageBitmap(BitmapUtil.getCircularBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length)));
        }

        // Calculating the place of the connected user
        int place = (accountsList.indexOf(currentAccount) + 1);

        // Displaying the place of the user
        tvPlace.setText(place + (place == 1 ? "st" : (place == 2) ? "nd" : (place == 3) ? "rd" : "th") + " place:");

        // Displaying the best time of the user
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
