package com.example.cora;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnNoteListener {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    TextView dayOW;
    TextView date;
    TextView noClass;
    ArrayList<Subject> subjectArrayList;
    boolean change = false; //for notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance();
        dayOW = findViewById(R.id.day_of_week);
        date = findViewById(R.id.date);
        noClass = findViewById(R.id.no_class_view);

        subjectArrayList = new ArrayList<>();

//        subjectArrayList.add(new Subject("8:30",false, false, "DM", 1));
//        subjectArrayList.add(new Subject("9:25",true,false,"JAVA",2));



        final DatabaseReference mRef = database.getReference("time_table");

        Calendar sCalendar = Calendar.getInstance();
        String dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        dayLongName = dayLongName.toLowerCase();
        dayLongName = "monday";
        dayOW.setText(dayLongName);

        Date d = new Date();
        CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());
        date.setText(s);

        mRef.child(dayLongName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int pos = -1;

                if(subjectArrayList.size()>0){

                    int count=0;
                    for(DataSnapshot s: snapshot.getChildren()){

                        Subject sub = s.getValue(Subject.class);

                        for(int i=0;i<subjectArrayList.size();i++){

                            if(sub.period==subjectArrayList.get(i).period){

                                if(sub.bunk!=subjectArrayList.get(i).bunk || sub.cancelled!=subjectArrayList.get(i).cancelled){

                                    if(sub.bunk) showNotification("Class Bunk!", sub.symbol+" class at "+ sub.time+ " has been bunked by class majority.");
                                    else if(sub.cancelled)  showNotification("Class Cancelled!", sub.symbol+" class at "+sub.time+" has been cancelled by the teacher.");
                                    else showNotification("Alas! Class to be held!", sub.symbol+" class at "+sub.time+ " will be held on time.");
                                    break;

                                }

                            }

                        }


                    }

                }


                subjectArrayList.clear();
                for(DataSnapshot s: snapshot.getChildren()){

                    Subject sub = s.getValue(Subject.class);
                    subjectArrayList.add(sub);

                }
                Collections.sort(subjectArrayList);

                MyAdapter adapter = new MyAdapter(subjectArrayList,MainActivity.this, MainActivity.this);
                recyclerView.setAdapter(adapter);

                if(subjectArrayList.size()==0) noClass.setVisibility(View.VISIBLE);
                else noClass.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
            }
        });




    }

    public void logout(View view){

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();


    }

    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    @Override
    public void OnNoteClick(int position) {

        positionClicked = position;
        final CharSequence[] list = new CharSequence[3];
        list[0] = "Bunk";
        list[1] = "Cancel";
        list[2] = "Will Occur";
        DatabaseReference userRef = database.getReference("users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot user:snapshot.getChildren()){

                    User u = user.getValue(User.class);
                    if(u.email.equals(mAuth.getCurrentUser().getEmail())) {

                        if(!u.CR){
                            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
                            materialAlertDialogBuilder.setTitle("You can not edit the classes as you are not CR!").setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                        }
                        else {
                            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(MainActivity.this);
                            materialAlertDialogBuilder.setNegativeButton("CANCEL",cancelClickListener).setSingleChoiceItems(list,0,selectedListener).setPositiveButton("OK",okListener).show();
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private int selected = 0, positionClicked;

    private DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    };

    private DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DatabaseReference ref = database.getReference("time_table").child(dayOW.getText().toString()).child(subjectArrayList.get(positionClicked).symbol.toLowerCase());


            if(selected==0){
                ref.child("bunk").setValue(true);
                ref.child("cancelled").setValue(false);
                change = true;
            }
            if(selected==1){
                ref.child("bunk").setValue(false);
                ref.child("cancelled").setValue(true);
                change = true;
            }
            if(selected==2){
                ref.child("bunk").setValue(false);
                ref.child("cancelled").setValue(false);
                change = true;
            }

        }
    };

    private DialogInterface.OnClickListener selectedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            selected=i;
        }
    };

    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.logo) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(counter, mBuilder.build());
        counter++;
    }

    int counter = 0;

}

