package com.example.vrtour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;
    private Button login;
    private TextView textView_donotH;

    //firebase

    private FirebaseAuth mAuth;
    //
    private ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();

        mdialog=new ProgressDialog(this);

        login();


    }
    private void login(){
        email=findViewById(R.id.Emaill_signup_edit_text);
   //     pass=findViewById(R.id.Password);
        login=findViewById(R.id.signupbtn);
        textView_donotH=findViewById(R.id.tv_have_account);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=email.getText().toString().trim();
                String Password=pass.getText().toString().trim();

                if (Email.isEmpty()){
                    email.setError("Required field..");
                    return;
                }

                if (Password.isEmpty()){
                    pass.setError("Required field..");
                    return;
                }


                mdialog.setMessage("Loading..");
                mdialog.show();


                    mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isComplete()){
                                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                mdialog.dismiss();
                                startActivity(new Intent(getApplicationContext(),MapActivity.class));

                            }

                            else {
                                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                mdialog.dismiss();

                            }

                        }
                    });

            }
        });


        textView_donotH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),SignupActivity.class));
            }
        });
    }

}
