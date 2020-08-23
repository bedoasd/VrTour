package com.example.vrtour;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //buttons signup&login

    private Button signup;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // signup();

        //login();


    }
    /*private void  signup(){

       // signup=findViewById(R.id.btn_log_in);
        signup.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),SignupActivity.class));
            }
        });


    }*/



    /*private void  login(){
     //   login=findViewById(R.id.btn_sign_up);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }*/
}
