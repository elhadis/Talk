package com.rgbat.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FindPepoleActivity extends AppCompatActivity {
    private RecyclerView findFriendList;
    private EditText searchEt;
    private  String str = "";
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pepole);
        searchEt = findViewById(R.id.search_user_text);
        findFriendList =findViewById(R.id.find_friend_list);
        findFriendList.setHasFixedSize(true);
        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSeq, int start, int before, int count) {

                if (searchEt.getText().toString().equals("")){
                    Toast.makeText(FindPepoleActivity.this, "please Write name To search", Toast.LENGTH_SHORT).show();
                }
                else {

                   str =  charSeq.toString();
                   onStart();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = null;
        if (str.equals("")){
            options= new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userRef,Contacts.class).build();
        }
        else {
            options =  new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(
                    userRef.orderByChild("name").startAt(str)
                    .endAt(str + "\uf8ff"),Contacts.class).build();
        }
        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final Contacts model) {

                        holder.userNameTet.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.profileImageView);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FindPepoleActivity.this,ProfileActivity.class);
                                String visit_user_id = getRef(position).getKey();
                                intent.putExtra("visit_user_id",visit_user_id);
                                intent.putExtra("profile_image",model.getImage());
                                intent.putExtra("profile_name",model.getName());
                                startActivity(intent);

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,
                                parent,false);
                        return new FindFriendViewHolder(view);
                    }
                };
        findFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTet;
        Button viioCallBtn;
        ImageView profileImageView;
        RelativeLayout cardView;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTet = itemView.findViewById(R.id.name_contact);
            viioCallBtn = itemView.findViewById(R.id.call_btn);

            profileImageView = itemView.findViewById(R.id.image_contact);
            cardView = itemView.findViewById(R.id.card_view1);
            viioCallBtn.setVisibility(View.GONE);
        }









    }

}
