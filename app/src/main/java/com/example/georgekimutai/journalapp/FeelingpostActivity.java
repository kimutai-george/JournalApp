package com.example.georgekimutai.journalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FeelingpostActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton addfeelingphoto;
    private EditText writesomething;
    private Button updatepost;

    private static final int Image_Picker=1;
    private Uri imageUri;
    private  String feelingtextbox;
    private StorageReference feelingsupdatesregerence;
    private String currentdate,currenttime,feelingtimename,downloadurl,Currentuser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userref,postref;
    private FirebaseAuth userAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feelingpost);

        addfeelingphoto= findViewById(R.id.addfeelingimage);
        writesomething= findViewById(R.id.txtwritefeeling);
        updatepost= findViewById(R.id.btnpostfeeling);
        feelingsupdatesregerence= FirebaseStorage.getInstance().getReference();
        userAuth=FirebaseAuth.getInstance();
        Currentuser=userAuth.getCurrentUser().getUid();
        userref=FirebaseDatabase.getInstance().getReference().child("Users");
        postref=FirebaseDatabase.getInstance().getReference().child("Posts");
        progressDialog=new ProgressDialog(this);



        toolbar= findViewById(R.id.postdataappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Feeling...");

        addfeelingphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        });
        updatepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Datavalidation();
            }
        });

    }

    private void Datavalidation() {
        feelingtextbox=writesomething.getText().toString();

        if (imageUri == null){
            Toast.makeText(FeelingpostActivity.this,"Please Choose an Image!!",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(feelingtextbox))
        {
            Toast.makeText(FeelingpostActivity.this,"Please Write Something!!",Toast.LENGTH_LONG).show();

        }
        else{
            progressDialog.setMessage("Updating Post...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            storefeelings();
        }
    }

    private void storefeelings() {
        Calendar cl=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mmmm-yyyy");
        currentdate=dateFormat.format(cl.getTime());

        Calendar ct=Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:MM");
        currenttime=timeFormat.format(ct.getTime());
        feelingtimename=currentdate+currenttime;
        StorageReference filepath=feelingsupdatesregerence.child("Post Images").child(imageUri.getLastPathSegment()+feelingtimename + ".jpg");
        filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    downloadurl=task.getResult().getDownloadUrl().toString();
                    Toast.makeText(FeelingpostActivity.this,"Your Post Updated!!",Toast.LENGTH_LONG).show();
                    savefellingcaption();
                    progressDialog.dismiss();

                }
                else {
                    String message=task.getException().getMessage();
                    Toast.makeText(FeelingpostActivity.this,"Error!!"+ message,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            }
        });
    }

    private void savefellingcaption() {
        userref.child(Currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String fullnames=dataSnapshot.child("fullnames").getValue().toString();
                    String profilepicture=dataSnapshot.child("profilepictures").getValue().toString();

                    HashMap postdata=new HashMap();
                    postdata.put("userid",Currentuser);
                    postdata.put("date",currentdate);
                    postdata.put("time",currenttime);
                    postdata.put("feelingsdecription",feelingtextbox);
                    postdata.put("postimage",downloadurl);
                    postdata.put("profilepicture",profilepicture);
                    postdata.put("fullnames",fullnames);

                    postref.child(Currentuser + feelingtimename).updateChildren(postdata)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        sendusertomainactivity();
                                        Toast.makeText(FeelingpostActivity.this,"New Post Updated Successfully!!",Toast.LENGTH_LONG).show();

                                    }
                                    else {
                                        String message=task.getException().getMessage();
                                        Toast.makeText(FeelingpostActivity.this,"Error:"+message,Toast.LENGTH_LONG).show();

                                    }
                                }
                            });




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void opengallery() {

        Intent profilepictureintent=new Intent();
        profilepictureintent.setAction(Intent.ACTION_GET_CONTENT);
        profilepictureintent.setType("image/*");
        startActivityForResult(profilepictureintent,Image_Picker);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Image_Picker && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            addfeelingphoto.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            sendusertomainactivity();

        }


        return super.onOptionsItemSelected(item);
    }

    private void sendusertomainactivity() {
        Intent intent=new Intent(FeelingpostActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
