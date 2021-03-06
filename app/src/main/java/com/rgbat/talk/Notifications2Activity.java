package com.rgbat.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Notifications2Activity extends AppCompatActivity {
    private RecyclerView notificationList;
    private DatabaseReference friendRequestRef,contactsRef,userRef;
    private String currentUserId;
    FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications2);
        notificationList = findViewById(R.id.notification_list);
        notificationList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();



    }

    @Override
    protected void onStart() {
        super.onStart();

  FirebaseRecyclerOptions  options= new FirebaseRecyclerOptions.Builder<Contacts>().
          setQuery(friendRequestRef.child(currentUserId),Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,NotificationViewHolder> firebaseRecyclerAdapter  = new
                FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int position, @NonNull Contacts model) {
                        holder.acceptBtn.setVisibility(View.VISIBLE);
                        holder.cancelBtn.setVisibility(View.VISIBLE);

                       final String  listUserId = getRef(position).getKey();

                        DatabaseReference requestTypeRef = getRef(position).child("request_type").getRef();
                        requestTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String type = snapshot.getValue().toString();


                                    if (type.equals("received")){

                                        holder.cardView.setVisibility(View.VISIBLE);



                                        userRef.child(listUserId).addValueEventListener(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if (snapshot.hasChild("image")){

                                                    final  String imageStr = snapshot.child("image").getValue().toString();

                                                    Picasso.get().load(imageStr).into(holder.profileImageView);

                                                }

                                                    final  String nameStr = snapshot.child("name").getValue().toString();
                                                    holder.userNameTet.setText(nameStr);

                                                holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        contactsRef.child(currentUserId).child(listUserId)
                                                                .child("Contact").setValue("Saved")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            contactsRef.child(listUserId).child(currentUserId)
                                                                                    .child("Contact").setValue("Saved")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                            friendRequestRef.child(currentUserId).child(listUserId)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()){

                                                                                                                friendRequestRef.child(listUserId).child(currentUserId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()){

                                                                                                                                    Toast.makeText(Notifications2Activity.this, " NewContacts Saved", Toast.LENGTH_SHORT).show();

                                                                                                                                }

                                                                                                                            }
                                                                                                                        });

                                                                                                            }

                                                                                                        }
                                                                                                    });



                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });



                                                    }
                                                });
                                                holder.cancelBtn.setOnClickListener(new View.OnClickListener() {


                                                    @Override
                                                    public void onClick(View v) {
                                                        friendRequestRef.child(currentUserId).child(listUserId)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            friendRequestRef.child(currentUserId).child(listUserId)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()){

                                                                                                Toast.makeText(Notifications2Activity.this, "Friend Request Canceled", Toast.LENGTH_SHORT).show();

                                                                                            }

                                                                                        }
                                                                                    });

                                                                        }

                                                                    }
                                                                });

                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });




                                    }
                                    else {

                                        holder.cardView.setVisibility(View.GONE);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_desigen,
                                parent,false);
                        return new NotificationViewHolder(view);

                    }
                };
        notificationList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();




    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTet;
        Button acceptBtn,cancelBtn;
        ImageView profileImageView;
        RelativeLayout cardView;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTet = itemView.findViewById(R.id.name_notification);
            acceptBtn = itemView.findViewById(R.id.request_accept_btn);
            cancelBtn = itemView.findViewById(R.id.request_decline_btn);
            profileImageView = itemView.findViewById(R.id.image_notification);
            cardView = itemView.findViewById(R.id.card_view);

        }

    }

//    private void CancelFriendRequest() {
//
//
//    }



//    private void AcceptFriendRequest() {
//
//
//
//
//    }

}
