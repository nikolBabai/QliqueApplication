package com.example.qlique;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qlique.Profile.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Intent i = getIntent();
        Event event = i.getParcelableExtra("event");
        ImageView membersInfo =findViewById(R.id.members_info);
        membersInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),EventMembers.class);
                i.putExtra("eventobj", (Parcelable) event);
                v.getContext().startActivity(i);
            }
        });
        FirebaseDatabase.getInstance().getReference("/users/"+ event.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.url!=null){
                   ImageView author_image =findViewById(R.id.user_profile_info);
                    Picasso.get().load(user.url).into(author_image);
                }
                TextView userName = findViewById(R.id.user_name_info);
                userName.setText(dataSnapshot.child("firstName").getValue().toString()+" "+dataSnapshot.child("lastName").getValue().toString());
                TextView desc = findViewById(R.id.description_post_info);
                ImageView photo_info =findViewById(R.id.image_home_info);
                Picasso.get().load( event.photoUrl).into(photo_info);
                desc.setText(event.description);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }
}