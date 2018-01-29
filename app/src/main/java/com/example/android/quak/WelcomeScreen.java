package com.example.android.quak;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





                Intent mainIntent = new Intent(WelcomeScreen.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }

}
