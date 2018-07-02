package com.example.georgekimutai.journalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Personaldetails extends AppCompatActivity {
    private Button savedata;
    private EditText txtfullname,txtusername,txtcountry,txtphonenumber,txtgender;
    private CircleImageView profilepicture;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    final static int Image_Picker=1;
    private StorageReference storageReference;
    String currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldetails);
        savedata= findViewById(R.id.btnsave);
        txtfullname= findViewById(R.id.txtfullnames);
        txtusername= findViewById(R.id.txtusername);
        txtcountry= findViewById(R.id.txtcountry);
        txtphonenumber= findViewById(R.id.txtphonenumber);
        profilepicture= findViewById(R.id.profileimage);
        txtgender= findViewById(R.id.txtgender);

        progressDialog=new ProgressDialog(this);



        firebaseAuth=FirebaseAuth.getInstance();
        currentuser=firebaseAuth.getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser);
        storageReference= FirebaseStorage.getInstance().getReference().child("profile pictures");

        
        savedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveuserpersonaldetails();
            }
        });
        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profilepictureintent=new Intent();
                profilepictureintent.setAction(Intent.ACTION_GET_CONTENT);
                profilepictureintent.setType("image/*");
                startActivityForResult(profilepictureintent,Image_Picker);
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profilepictures")) {
                        String imageurl = dataSnapshot.child("profilepictures").getValue().toString();
                        Picasso.get().load(imageurl)
                                .placeholder(R.drawable.profile)
                                .into(profilepicture);
                    }
                    else {
                        Toast.makeText(Personaldetails.this,"Please Select a Profile Picture",Toast.LENGTH_LONG).show();


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Image_Picker && resultCode==RESULT_OK && data!=null){
            Uri uriimage=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                progressDialog.setMessage("Updating your Profile Picture...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);
                Uri  finalimageUri=result.getUri();
                currentuser=firebaseAuth.getCurrentUser().getUid();
                StorageReference imagedb=storageReference.child(currentuser + ".jpg");
                imagedb.putFile(finalimageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Personaldetails.this,"Profile Picture Updated Successfully",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            final String urldownload=task.getResult().getDownloadUrl().toString();
                            databaseReference.child("profilepictures").setValue(urldownload)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Intent intent=new Intent(Personaldetails.this, Personaldetails.class);
                                                startActivity(intent);
                                                Toast.makeText(Personaldetails.this,"Profile Picture Updated Successfully",Toast.LENGTH_LONG).show();
                                                progressDialog.dismiss();
                                            }else {
                                                String message=task.getException().getMessage();
                                                Toast.makeText(Personaldetails.this,"Profile Picture Updated Not Successfull..." + message,Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        }else {
            Toast.makeText(Personaldetails.this,"Profile Picture Cropping Not Successfully",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    private void saveuserpersonaldetails() {
        String fullnames=txtfullname.getText().toString();
        String username=txtusername.getText().toString();
        String country=txtcountry.getText().toString();
        String phonenumber=txtphonenumber.getText().toString();
        String gender=txtgender.getText().toString();




        if (TextUtils.isEmpty(fullnames)){
            Toast.makeText(Personaldetails.this,"Full Names are Required...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(username)){
            Toast.makeText(Personaldetails.this,"Username is Required...", Toast.LENGTH_LONG).show();

        }
        else if (TextUtils.isEmpty(country)){
            Toast.makeText(Personaldetails.this,"Country Cannot be Empty...", Toast.LENGTH_LONG).show();

        }
        else if (TextUtils.isEmpty(phonenumber)){
            Toast.makeText(Personaldetails.this,"PhoneNumber Cannot be Empty...", Toast.LENGTH_LONG).show();

        }
        else if (TextUtils.isEmpty(gender)){
            Toast.makeText(Personaldetails.this,"Gender Cannot be Empty...", Toast.LENGTH_LONG).show();

        }
        else {
            progressDialog.setMessage("Updating your Data...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            HashMap savedata=new HashMap();
            savedata.put("fullnames",fullnames);
            savedata.put("username",username);
            savedata.put("country",country);
            savedata.put("phonenumber",phonenumber);
            savedata.put("status","Android Apps Development is Passion");
            savedata.put("gender",gender);


            databaseReference.updateChildren(savedata).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        redirrectuser();
                        Toast.makeText(Personaldetails.this,"Your Information Have Been Updated",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }else {
                        String message=task.getException().getMessage();
                        Toast.makeText(Personaldetails.this,"Your Information Update Failed"+message ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });





        }


    }



    private void redirrectuser() {
        Intent userintent=new Intent(Personaldetails.this,MainActivity.class);
        userintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(userintent);
        finish();
    }
}
