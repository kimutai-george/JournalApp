package com.example.georgekimutai.journalapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView listrecyclerview;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef,postRef;
    private ProgressDialog progressDialog;
    private CircleImageView naveprofilepicture;
    private TextView tvnav_usernames;
    private ImageButton addpost;

    String CurrenuserID;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar= findViewById(R.id.mainappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Journal");
        mAuth=FirebaseAuth.getInstance();
        CurrenuserID=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        postRef.keepSynced(true);
        progressDialog=new ProgressDialog(this);
        addpost= findViewById(R.id.addpostbutton);
        listrecyclerview= findViewById(R.id.alluserslistrecyclerview);
        listrecyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        listrecyclerview.setLayoutManager(linearLayoutManager);


        drawerLayout= findViewById(R.id.maindrawer);
        navigationView= findViewById(R.id.navigationitem);
        listrecyclerview= findViewById(R.id.alluserslistrecyclerview);

        View view=navigationView.inflateHeaderView(R.layout.headerlayout);
        tvnav_usernames= view.findViewById(R.id.navuserfullnames);
        naveprofilepicture= view.findViewById(R.id.navprofileimage);




        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.Draweropen,R.string.Drawerclosed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        userRef.child(CurrenuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("fullnames")) {
                        String fullnames = dataSnapshot.child("fullnames").getValue().toString();
                        tvnav_usernames.setText(fullnames);

                    }
                    if (dataSnapshot.hasChild("profilepictures")) {
                        String profilepicture = dataSnapshot.child("profilepictures").getValue().toString();


                        Picasso.get().load(profilepicture)
                                .placeholder(R.drawable.profile)
                                .into(naveprofilepicture);
                    }
                    else {
                        Toast.makeText(MainActivity.this,"Profile name doesnot exist",Toast.LENGTH_LONG).show();
                    }
                }
            }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenu(item);
                return false;
            }
        });

        addpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectusertofeelingactivity();
            }
        });


        displayallposts();


    }

    private void displayallposts(){
        FirebaseRecyclerAdapter<feelingposts,postviewholder>firebaseRecyclerAdapter=new
                FirebaseRecyclerAdapter<feelingposts, postviewholder>(
                        feelingposts.class,
                        R.layout.allpostlayout,
                        postviewholder.class,
                        postRef
                ) {
                    @Override
                    protected void populateViewHolder(postviewholder viewHolder, feelingposts model, int position) {
                        viewHolder.setFullnames(model.getFullnames());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setFeelingsdescription(model.getFeelingsdescription());
                        viewHolder.setProfilepicture(getApplicationContext(),model.getProfilepicture());
                        viewHolder.setPostimage(getApplicationContext(),model.getPostimage());
                    }
                };
        listrecyclerview.setAdapter(firebaseRecyclerAdapter);

    }
    private void Displayposts() {

       /* FirebaseRecyclerOptions<feelingposts>options=new FirebaseRecyclerOptions.Builder<feelingposts>()
                .setQuery(postRef,feelingposts.class)
                .build();*/
       /* FirebaseRecyclerAdapter<feelingposts,postviewholder> firebaseRecyclerAdapter=new
                FirebaseRecyclerAdapter<feelingposts, postviewholder>(
                        feelingposts.class,
                        R.layout.allpostlayout,
                        postviewholder.class,
                        postRef
                ) {
                    @Override
                    protected void populateView(postviewholder holder, int position,feelingposts model) {
                        holder.setFullnames(model.getFullnames());
                        holder.setDate(model.getDate());
                        holder.setTime(model.getTime());
                        holder.setFeelingsdescription(model.getFeelingsdescription());
                        holder.setProfilepicture(getApplicationContext(),model.getProfilepicture());
                        holder.setPostimage(getApplicationContext(),model.getPostimage());

                    }
                    @Override
                    protected void onBindViewHolder(@NonNull postviewholder holder, int position, @NonNull feelingposts model) {


                    }

                    @NonNull
                    @Override
                    public postviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return null;
                    }
                };*/



        //listrecyclerview.setAdapter(firebaseRecyclerAdapter);
    }
    public static class postviewholder extends RecyclerView.ViewHolder{

        View view;
        public postviewholder(View itemView) {
            super(itemView);
            view=itemView;
        }
        public void setFullnames(String fullnames) {

            TextView fullname= view.findViewById(R.id.postusername);
            fullname.setText(fullnames);
        }
        public void setProfilepicture(Context applicationContext, String profilepicture) {
            CircleImageView profilepic= view.findViewById(R.id.postprofileimage);
            Picasso.get().load(profilepicture)
                    .into(profilepic);
        }
        public void setTime(String time) {

            TextView posttime= view.findViewById(R.id.tvpostedtime);
            posttime.setText("  " + time);

        }
        public void setDate(String date) {
            TextView postdate= view.findViewById(R.id.tvposteddate);
            postdate.setText("  " + date);
        }
        public void setFeelingsdescription(String feelingsdescription) {
            TextView feelings= view.findViewById(R.id.tvpostcaption);
            feelings.setText(feelingsdescription);
        }
        public void setPostimage(Context applicationContext, String postimage) {
            ImageView posterimage= view.findViewById(R.id.postedimage);
            Picasso.get().load(postimage)
                    .into(posterimage);
        }


    }

    private void redirectusertofeelingactivity() {
        Intent feelingintent=new Intent(MainActivity.this,FeelingpostActivity.class);
        startActivity(feelingintent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if (firebaseUser==null){
            userredirect();
        }else{
            checkifuserexist();
        }
    }

    private void checkifuserexist() {
        final String currentuser=mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentuser)){
                    userredirecttopersonaldetails();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void userredirecttopersonaldetails() {
        Intent personaldetailsintent=new Intent(MainActivity.this,Personaldetails.class);
        personaldetailsintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(personaldetailsintent);
        finish();
    }

    private void userredirect() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenu(MenuItem item) {
        switch (item.getItemId()){
            case R.id.newpost:
              redirectusertofeelingactivity();
                break;
            case R.id.myprofile:
                Toast.makeText(this,"Profile",Toast.LENGTH_LONG).show();
                break;
            case R.id.menuhome:
                Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
                break;
            case R.id.friendsmenu:
                Toast.makeText(this,"Friends",Toast.LENGTH_LONG).show();
                break;
            case R.id.findfriendsmenu:
                Toast.makeText(this,"Find Friends",Toast.LENGTH_LONG).show();
                break;
            case R.id.messagesmenu:
                Toast.makeText(this,"Messages",Toast.LENGTH_LONG).show();
                break;
            case R.id.settingsmenu:
                Toast.makeText(this,"Settings",Toast.LENGTH_LONG).show();
                break;
            case R.id.logout:
                progressDialog.setMessage("Signing Out...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(true);
               mAuth.signOut();
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                progressDialog.hide();
                finish();

                break;
        }
    }
}
