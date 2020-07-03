package com.rgbat.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {
    private TextView nameContact;
    private ImageView profileImage;
    private ImageView cancelCallBtn,makeCallBtn;
    private String receiverUserId = "",receiverUserImage = "",receiverUserName = "";
    private String senderUserId = "",senderUserImage = "",senderUserName = "";
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        nameContact = findViewById(R.id.name_calling);
        profileImage = findViewById(R.id.profile_image_calling);
        cancelCallBtn = findViewById(R.id.cancel_call);
        makeCallBtn = findViewById(R.id.make_call);

        receiverUserId  = getIntent().getExtras().get("visit_user_id").toString();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        getAndUserProfileInfo();


    }

    private void getAndUserProfileInfo() {



        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(receiverUserId).exists()){
                    receiverUserImage = snapshot.child(receiverUserId).child("image").getValue().toString();
                    receiverUserName = snapshot.child(receiverUserId).child("name").getValue().toString();
                    nameContact.setText(receiverUserName);
                    Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile).into(profileImage);

                }
                if (snapshot.child(senderUserId).exists()){
                    senderUserImage = snapshot.child(senderUserId).child("image").getValue().toString();
                    senderUserName = snapshot.child(senderUserId).child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        userRef.child(receiverUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")){

                            final HashMap<String,Object> callingInfo = new HashMap<>();

                            callingInfo.put("calling",receiverUserId);

                            userRef.child(senderUserId).child("Calling")
                                    .updateChildren(callingInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){


                                                final HashMap<String,Object> ringingInfo = new HashMap<>();
                                               

                                                ringingInfo.put("ringing",senderUserId);

                                                userRef.child(receiverUserId)
                                                        .child("Ringing")
                                                        .updateChildren(ringingInfo);



                                            }

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
