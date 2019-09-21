package com.example.thethreemusketeers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView newuser,forgotpassword;
    private EditText username,password;
    private Button loginbtn;
    //private FirebaseAuth firebaseAuth;


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
        //firebaseAuth=FirebaseAuth.getInstance();
        //if(firebaseAuth.getCurrentUser()!=null && firebaseAuth.getCurrentUser().isEmailVerified())
        //{
        //    finish();
        //    startActivity(new Intent(LoginAcitivity.this,ProfileActivity.class));
        //}
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
        else if(view==loginbtn);//userLogin();
        else if(view==forgotpassword) { startActivity((new Intent(LoginActivity.this,ResetActivity.class)));}
        //else if(view==googleloginbtn)googleLogin();
    }

}
