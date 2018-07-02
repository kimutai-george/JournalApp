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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private Button btnlogin;
    private EditText txtemail,txtpassword;
    private TextView tvregister;
    private FirebaseAuth userAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnlogin= findViewById(R.id.btnlogin);
        txtemail= findViewById(R.id.txtemail);
        txtpassword= findViewById(R.id.txtpassword);
        tvregister= findViewById(R.id.tvregister);
        userAuth=FirebaseAuth.getInstance();
        progressDialog=new  ProgressDialog(this);
        firebaseDatabase=FirebaseDatabase.getInstance().getReference();

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=txtemail.getText().toString();
                String password=txtpassword.getText().toString();


                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"Email Cannot be Empty...", Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Password Cannot be Empty...", Toast.LENGTH_LONG).show();

                }else {
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(true);
                    userAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this,"Login Successfull...", Toast.LENGTH_LONG).show();
                                        userredirect();
                                        progressDialog.dismiss();

                                    }
                                    else {
                                        String message=task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this,"Login Fail..." + message, Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();

                                    }
                                }
                            });
                }

            }
        });
    }

    private void userredirect() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
