package com.github.jc.cryptopea.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jc.cryptopea.Models.ProfileDetails;
import com.github.jc.cryptopea.R;
import com.github.jc.cryptopea.Utils.Constants;

public class Profile extends AppCompatActivity {

    private Constants mConstants;
    private ProfileDetails profileDetails;

    private ImageView imgProfilePic;
    private TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mConstants = new Constants(this);
        profileDetails = ProfileDetails.getInstance();

        imgProfilePic = findViewById(R.id.imgProfilePic);
        mConstants.GlideImageLoader(profileDetails.getPhoto_url(), imgProfilePic);
        tvName = findViewById(R.id.tvName);
        tvName.setText(profileDetails.getDisplay_name());
        tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(profileDetails.getEmail());
    }
}
