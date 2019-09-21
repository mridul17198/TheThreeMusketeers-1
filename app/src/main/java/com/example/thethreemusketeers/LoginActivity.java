package com.example.thethreemusketeers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView newuser,forgotpassword;
    private EditText username,password;
    private Button loginbtn;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUI();
        setupListners();
    }

    private void setupUI() {

        newuser=(TextView)findViewById(R.id.id_login_newuser);
        username=(EditText)findViewById(R.id.id_login_username);
        password=(EditText)findViewById(R.id.id_login_password);
        loginbtn=(Button)findViewById(R.id.id_login_loginbtn);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null && firebaseAuth.getCurrentUser().isEmailVerified())
        {
            finish();
            //startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
        }
        forgotpassword=(TextView)findViewById(R.id.id_login_forgotpassword);

    }

    private void setupListners()
    {
        newuser.setOnClickListener(this);
        loginbtn.setOnClickListener(this);
        forgotpassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        if(view==newuser) { startActivity(new Intent(LoginActivity.this,RegisterationActivity.class)); }
        else if(view==loginbtn)userLogin();
        else if(view==forgotpassword) { startActivity((new Intent(LoginActivity.this,ResetActivity.class)));}
    }

    private void userLogin(){

        String String_email=username.getText().toString().trim();
        String String_passowrd=password.getText().toString().trim();

        if(validateEmailPassword(String_email,String_passowrd))
        {
            firebaseAuth.signInWithEmailAndPassword(String_email,String_passowrd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                                }
                                else Toast.makeText(LoginActivity.this,"Please Verify You Email Address.",Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateEmailPassword(String String_email,String String_password)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        String passwordRegex = "((?=.*[a-z])" +
                "(?=.*[@#$%_])" +             //"(?=.*\\d)"+"(?=.*[A-Z])"
                ".{6,20})";

        Pattern email_pat = Pattern.compile(emailRegex);
        Pattern password_pat=Pattern.compile(passwordRegex);
        if (String_email == null || String_password==null) return false;
        boolean check_email=email_pat.matcher(String_email).matches();
        boolean check_password=password_pat.matcher(String_password).matches();

        if(!check_email) Toast.makeText(LoginActivity.this,"Please Enter Valid Email",Toast.LENGTH_SHORT).show();
        else if(!check_password)Toast.makeText(LoginActivity.this,"Please Enter Valid Password",Toast.LENGTH_SHORT).show();
        else return true;
        return false;
    }

}
