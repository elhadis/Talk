package com.rgbat.talk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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


import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private Button saveBtn;
    private EditText userNameEt,userBioEt;
    private ImageView profileImageView;
    private static int GalleryPick  = 1;
    private Uri ImageUri;
    private StorageReference userProfileImageRef;
    private String downloadUrl;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveBtn = findViewById(R.id.save_Settings_btn);
        userNameEt= findViewById(R.id.username_Settings);
        userBioEt = findViewById(R.id.bio_Settings);
        profileImageView = findViewById(R.id.setting_profile_image);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog = new ProgressDialog(this);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();

            }
        });
        saveInfoOnly();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            profileImageView.setImageURI(ImageUri);
        }
    }
    private void saveUserData() {

        final String getUserName = userNameEt.getText().toString();
        final String getUserStatus = userBioEt.getText().toString();
        if (ImageUri == null){

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")){

                        saveInfoOnly();

                    }
                    else {
                        Toast.makeText(SettingsActivity.this, "please Select Image", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else if (getUserName.equals("")){
            userNameEt.setError("please Write Your Name");
            return;
        }else if (getUserStatus.equals("")){
            userBioEt.setError("Write Your Status");
            return;
        }
        else {

            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("please Wait.....");
            progressDialog.show();

            final StorageReference fillPath  =  userProfileImageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            final UploadTask uploadTask = fillPath.putFile(ImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }


                        downloadUrl = fillPath.getDownloadUrl().toString();
                        return fillPath.getDownloadUrl();



                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        downloadUrl = task.getResult().toString();

                        final HashMap<String,Object> profileMap = new HashMap<>();
                        profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name",getUserName);
                        profileMap.put("status",getUserStatus);
                        profileMap.put("image",downloadUrl);
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(SettingsActivity.this,ContectsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Profile Image Has Been Update it", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                }
            });
        }





    }

    private void saveInfoOnly() {

        final String getUserName = userNameEt.getText().toString();
        final String getUserStatus = userBioEt.getText().toString();



        if (getUserName.equals("")){
            userNameEt.setError("please Write Your Name");
            return;
        }else if (getUserStatus.equals("")){
            userBioEt.setError("Write Your Status");
            return;
        }
        else {

            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("please Wait.....");
            progressDialog.show();


            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name",getUserName);
            profileMap.put("status",getUserStatus);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(SettingsActivity.this,ContectsActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Profile Image Has Been Update it", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }
    private void retrieveUserInfo(){
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String imageDb = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();
                            String baio = snapshot.child("status").getValue().toString();
                            userNameEt.setText(name);
                            userBioEt.setText(baio);
                            Picasso.get().load(imageDb).placeholder(R.drawable.profile).into(profileImageView);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}
