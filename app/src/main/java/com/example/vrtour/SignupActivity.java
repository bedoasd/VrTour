package com.example.vrtour;

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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {


    private EditText email;
    private EditText passsw;
    private TextView textView;
    private Button signupbtn ;
    LoginButton loginButton;
    SignInButton signInButton;
    GoogleSignInClient signInClient;
    private String Tag = "SignUpActivity";
    private int RC_SIGN_IN = 1 ;
    //dialog
      ProgressDialog mdialog;

    //firebase

    private FirebaseAuth mAuth;


    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
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

        mAuth=FirebaseAuth.getInstance();
        Signup();
        mdialog=new ProgressDialog(this);


    }



    private void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                ParseUser user = ParseUser.getCurrentUser();
                try{
                    user.setUsername(object.getString("name"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                try{
                    user.setEmail(object.getString("email"));
                }catch(JSONException e){
                    e.printStackTrace();
                }
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        alertDisplayer("First Time Login!", "Welcome!");
                    }
                });
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(SignupActivity.this, MapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
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
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    Toast.makeText(SignupActivity.this, "Good", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    Intent a = new Intent(SignupActivity.this,MapActivity.class);
                    startActivity(a);
                }
                else
                {
                    Toast.makeText(SignupActivity.this, "Faild", Toast.LENGTH_SHORT).show();
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

    private static final String EMAIL = "email";



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
                            startActivity(new Intent(getApplicationContext(),MapActivity.class));

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
