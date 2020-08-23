package com.example.vrtour.myinsta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vrtour.MapActivity;
import com.example.vrtour.R;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username,fullname,email,password;
    private Button register;
    private TextView txt_login;

    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog pd;

    SignInButton signInButton;
    GoogleSignInClient signInClient;
    private String Tag = "SignUpActivity";
    private int RC_SIGN_IN = 1 ;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        signInButton = findViewById(R.id.signG);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = signInClient.getSignInIntent();
                startActivityForResult(signIntent,RC_SIGN_IN);
            }
        });

        callbackManager = CallbackManager.Factory.create();


        username=findViewById(R.id.username);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        txt_login=findViewById(R.id.txt_login);

        auth= FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(RegisterActivity.this);
                pd.setMessage("please Wait");
                pd.show();

                String str_username=username.getText().toString().trim();
                String str_fullname=fullname.getText().toString().trim();
                String str_email=email.getText().toString().trim();
                String str_password=password.getText().toString().trim();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                {
                    Toast.makeText(RegisterActivity.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }
                else if (str_password.length()<6){
                    Toast.makeText(RegisterActivity.this, "password must be 6 characters", Toast.LENGTH_SHORT).show();
                }

                else {

                        Register(str_username,str_fullname,str_email,str_password);
                }
            
            }
        });

    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(RegisterActivity.this, MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handlesignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handlesignInResult(Task<GoogleSignInAccount> comp) {
        try {
            GoogleSignInAccount acc = comp.getResult(ApiException.class);
            Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch (ApiException e)
        {

        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    Toast.makeText(RegisterActivity.this, "Good", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                    Intent a = new Intent(RegisterActivity.this,MapActivity.class);
                    startActivity(a);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Faild", Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });
    }

    private void updateUI(FirebaseUser o) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null)
        {
            String personName = account.getDisplayName();
            String personGiveName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri PersonPhoto = account.getPhotoUrl();

            Toast.makeText(this, personName + personEmail, Toast.LENGTH_SHORT).show();
        }
    }




    private void Register(final String username, final String fullname , String email , String password ){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    String userid=firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                    HashMap<String,Object>hashMap=new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username.toLowerCase());
                    hashMap.put("fullname",fullname);
                    hashMap.put("bio"," ");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/instagram-feb64.appspot.com/o/placeholde.png?alt=media&token=f6cc4dd7-d4e5-4851-a9d4-654de23adf73");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                Intent intent=new Intent(RegisterActivity.this, MapActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "You can't Register with this Email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}