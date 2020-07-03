package com.rgbat.talk;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContectsActivity extends AppCompatActivity {
    BottomNavigationView navView;
    private RecyclerView myContactsList;
    private ImageView findPeopleBtn;
    DatabaseReference contactsRef,userRef;
    FirebaseAuth mAuth;
    private String currentUserId;
    private  String userName = "",profileImage = "" ,callBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         navView = findViewById(R.id.nav_view);
         navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
         findPeopleBtn = findViewById(R.id.find_people_btn);
         myContactsList = findViewById(R.id.contacts_list);
         myContactsList.setHasFixedSize(true);
         myContactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
       // NavigationUI.setupWithNavController(navView, navController);
        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),FindPepoleActivity.class);
                startActivity(intent);
            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            Intent intent= new Intent(ContectsActivity.this, ContectsActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.navigation_settings:
                            Intent settingIntent= new Intent(ContectsActivity.this,SettingsActivity.class);
                            startActivity(settingIntent);
                            break;
                        case R.id.navigation_notifications:
                            Intent notificationIntent= new Intent(ContectsActivity.this,Notifications2Activity.class);
                            startActivity(notificationIntent);
                            break;
                        case R.id.navigation_logout:
                            FirebaseAuth.getInstance().signOut();
                            Intent logoutIntent= new Intent(ContectsActivity.this,RegistrationActivity.class);
                            startActivity(logoutIntent);
                            finish();
                            break;

                    }
                    return true;
                }
            };

    @Override
    protected void onStart() {

        super.onStart();


        checkForRecevingCall();


        validateUser();



        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef.child(currentUserId),Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                        final String listUserId = getRef(position).getKey();
                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()){

                                    userName= snapshot.child("name").getValue().toString();
                                    profileImage= snapshot.child("image").getValue().toString();
                                    holder.userNameTet.setText(userName);
                                    Picasso.get().load(profileImage).into(holder.profileImageView);

                                }
                                holder.callBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent callingIntent = new Intent(ContectsActivity.this,CallingActivity.class);
                                        callingIntent.putExtra("visit_user_id",listUserId);
                                        startActivity(callingIntent);

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,
                                parent,false);
                        return new ContactsViewHolder(view);
                    }
                };
        myContactsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void checkForRecevingCall() {

        userRef.child(currentUserId)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.hasChild("ringing")){
                            callBy = snapshot.child("ringing").getValue().toString();

                            Intent callingIntent = new Intent(ContectsActivity.this,CallingActivity.class);
                            callingIntent.putExtra("visit_user_id",callBy);
                            startActivity(callingIntent);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void validateUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()){
                            Intent settingsIntent = new Intent(getBaseContext(),SettingsActivity.class);
                            startActivity(settingsIntent);
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTet;
        Button callBtn;
        ImageView profileImageView;
        RelativeLayout cardView;

        public ContactsViewHolder(@NonNull View itemView) {

            super(itemView);

            userNameTet = itemView.findViewById(R.id.name_contact);
            callBtn = itemView.findViewById(R.id.call_btn);

            profileImageView = itemView.findViewById(R.id.image_contact);


        }
    }

}
