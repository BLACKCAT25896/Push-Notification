package com.example.pushnotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.pushnotification.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<String>userNameList;
    private List<String>userIds;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        init();
        getUsers();
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this, ""+userIds.get(i), Toast.LENGTH_SHORT).show();
                Map<String,Object>notificationMap = new HashMap<>();
                notificationMap.put("message","this is for test notification...");
                notificationMap.put("senderId",userId);

                databaseReference.child("users").child(userIds.get(i)).child("notification").push().setValue(notificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });

    }

    private void initListView() {
        ArrayAdapter arrayAdapter =new ArrayAdapter(this,android.R.layout.simple_list_item_1,userNameList);
        binding.listView.setAdapter(arrayAdapter);
    }

    private void getUsers() {
        DatabaseReference userRef = databaseReference.child("users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    userNameList.clear();
                    userIds.clear();

                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        String name = data.child("name").getValue().toString();
                        String id = data.getKey();
                        userNameList.add(name);
                        userIds.add(id);
                    }
                    initListView();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        userNameList = new ArrayList<>();
        userIds = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userId = firebaseAuth.getCurrentUser().getUid();


    }

    public void setting(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.setting:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                        finish();
                        return true;


                    default:
                }

                return false;
            }
        });
        popupMenu.inflate(R.menu.setting);
        popupMenu.show();

    }
}
