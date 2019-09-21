package com.example.thethreemusketeers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegisterationActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText email,password,mobile;
    private Button regbtn,verify_mobile;
    private TextView loginuser;
    private FirebaseAuth firebaseauth;

    AutoCompleteTextView address;
    ArrayList<String> locations_arr;
    DatabaseReference databaseReference;
    String String_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        setupUI();
        setupListners();
    }

    private void setupUI() {

        firebaseauth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("User_Registeration_Information");
        email=(EditText)findViewById(R.id.id_register_email);
        password=(EditText)findViewById(R.id.id_register_password);
        regbtn=(Button)findViewById(R.id.id_register_btn);
        loginuser=(TextView)findViewById(R.id.id_register_loginuser);
        mobile=(EditText)findViewById(R.id.id_register_mobilenum);
        verify_mobile=(Button)findViewById(R.id.id_register_sendotpbtn);

        address=(AutoCompleteTextView)findViewById(R.id.id_register_address);

        locations_arr=new ArrayList<String>();
        try
        {
            BufferedReader reader=new BufferedReader(new InputStreamReader(getAssets().open("locations_file")));
            String alocation;
            while((alocation=reader.readLine())!=null)locations_arr.add(alocation);
        }
        catch (Exception e)
        { Toast.makeText(RegisterationActivity.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show(); }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,locations_arr);
        address.setAdapter(adapter);
        address.setThreshold(2);

        email.setEnabled(false);
        password.setEnabled(false);
        regbtn.setEnabled(false);

    }

    private void setupListners() {

        regbtn.setOnClickListener(this);
        loginuser.setOnClickListener(this);
        verify_mobile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == regbtn) {

            final String String_email = email.getText().toString().trim();
            String String_password =password.getText().toString().trim();
            final String String_location =address.getText().toString().trim();


            if (validateEmailPassword(String_email, String_password) && validateaddress(String_location)) {

                firebaseauth.createUserWithEmailAndPassword(String_email, String_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterationActivity.this, "Please Check Your Email For Verification.", Toast.LENGTH_SHORT).show();
                                        database_user_registeration_information obj = new database_user_registeration_information(String_email,String_mobile,String_location);
                                        databaseReference.child("user"+firebaseauth.getCurrentUser().getUid()).setValue(obj);
                                        firebaseauth.signOut();
                                        Toast.makeText(RegisterationActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                        Intent intent=new Intent(RegisterationActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else Toast.makeText(RegisterationActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterationActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Handler lag_for_toast = new Handler();
                            lag_for_toast.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        }
                    }
                });
            }
        }
        else if (view == loginuser)
        {
            finish();
            Intent intent=new Intent(RegisterationActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(view==verify_mobile)
        {
            String_mobile=mobile.getText().toString().trim();
            if(validateIndianmobilenumber(String_mobile))
            {
                String_mobile="+91"+String_mobile;
                mobile.setEnabled(false);
                verify_mobile.setEnabled(false);


                PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        Toast.makeText(RegisterationActivity.this,"Code Sent!Wait for 60 Seconds.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(RegisterationActivity.this,"Mobile Verified!",Toast.LENGTH_SHORT).show();
                        verify_mobile.setText("Verified");
                        email.setEnabled(true);
                        password.setEnabled(true);
                        regbtn.setEnabled(true);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        mobile.setEnabled(true);
                        verify_mobile.setEnabled(true);
                        Toast.makeText(RegisterationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                };
                PhoneAuthProvider.getInstance().verifyPhoneNumber(String_mobile,60, TimeUnit.SECONDS,RegisterationActivity.this,mcallbacks);

            }
        }
    }
    private boolean validateEmailPassword(String String_email,String String_password)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";


        String passwordRegex = "((?=.*[a-z])" +
                "(?=.*[@#$%_])" +
                ".{6,20})";

        Pattern email_pat = Pattern.compile(emailRegex);
        Pattern password_pat=Pattern.compile(passwordRegex);
        if (String_email == null || String_password==null) return false;
        boolean check_email=email_pat.matcher(String_email).matches();
        boolean check_password=password_pat.matcher(String_password).matches();

        if(!check_email)Toast.makeText(RegisterationActivity.this,"Enter Valid Email",Toast.LENGTH_SHORT).show();
        else if(!check_password)Toast.makeText(RegisterationActivity.this,"Enter Valid Password",Toast.LENGTH_SHORT).show();
        else return true;
        return false;
    }
    private boolean validateIndianmobilenumber(String String_mobile)
    {
        String mobileRegex="[7-9][0-9]{9}";
        Pattern mobile_pat=Pattern.compile(mobileRegex);
        if(String_mobile==null)return false;
        boolean check_mobile=mobile_pat.matcher(String_mobile).matches();
        if(!check_mobile)
        {
            Toast.makeText(RegisterationActivity.this,"Enter Valid Mobile Number",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private boolean validateaddress(String String_location){

        int i;
        for(i=0;i<locations_arr.size();i++)if(String_location.equals(locations_arr.get(i)))break;
        if(i==locations_arr.size())
        {
            Toast.makeText(RegisterationActivity.this,"Choose Valid Address",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
