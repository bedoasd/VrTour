package com.example.vrtour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {


    private EditText email;
    private EditText passsw;
    private TextView textView;
    private Button signupbtn;

    //dialog
      ProgressDialog mdialog;

    //firebase

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth=FirebaseAuth.getInstance();

        Signup();

        mdialog=new ProgressDialog(this);



    }

    //sign up implementation method

    private void Signup(){
        email=findViewById(R.id.Emaill_signup_edit_text);
        passsw=findViewById(R.id.password_signup_edit_text);
        textView=findViewById(R.id.tv_have_account);
        signupbtn=findViewById(R.id.signupbtn);

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emaill=email.getText().toString().trim();
                String passs=passsw.getText().toString().trim();

                if (TextUtils.isEmpty(emaill)){
                    email.setError("Required Field ...");
                    return;
                }
                if(TextUtils.isEmpty(passs)){
                    passsw.setError("Required Field ...");
                    return;
                }

                   mdialog.setMessage("Loading..");
                  mdialog.show();


                mAuth.createUserWithEmailAndPassword(emaill,passs).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isComplete()){
                            Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                               mdialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),HomeMapsActivity.class));

                        }
                        else{
                            Toast.makeText(SignupActivity.this, "Failed....", Toast.LENGTH_SHORT).show();
                           mdialog.dismiss();
                        }
                    }
                });

            }
        });




        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

    }
}
