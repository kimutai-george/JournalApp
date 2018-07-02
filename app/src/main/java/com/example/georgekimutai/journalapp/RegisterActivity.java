package com.example.georgekimutai.journalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText txtemail,txtpassword,txtconfirmpassword;
    private Button register;
    private FirebaseAuth userauth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register= findViewById(R.id.btnregister);
        txtemail= findViewById(R.id.txtemail);
        txtpassword= findViewById(R.id.txtpassword);
        txtconfirmpassword= findViewById(R.id.txtconfirmpassword);
        progressDialog=new ProgressDialog(this);


        userauth=FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=txtemail.getText().toString();
                String password=txtpassword.getText().toString();
                String confirmpassword=txtconfirmpassword.getText().toString();


                if (TextUtils.isEmpty(email)){
                  Toast.makeText(RegisterActivity.this,"Email Cannot be Empty...", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this,"Password Cannot be Empty...", Toast.LENGTH_LONG).show();

                }
                else if (TextUtils.isEmpty(confirmpassword)){
                    Toast.makeText(RegisterActivity.this,"Please Confirm Your Password...", Toast.LENGTH_LONG).show();

                }
                else if (!password.equals(confirmpassword)){
                    Toast.makeText(RegisterActivity.this,"Password do not match...", Toast.LENGTH_LONG).show();

                }
                else {
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(true);

                    userauth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                       Toast.makeText(RegisterActivity.this,"Account Creation Was Successfull...", Toast.LENGTH_LONG).show();
                                        userredirect();
                                  progressDialog.dismiss();
                                    }else {
                                        String message=task.getException().getMessage();
                          Toast.makeText(RegisterActivity.this,"Account Creation Was not Successfull..." + message, Toast.LENGTH_LONG).show();
                          progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void userredirect() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
