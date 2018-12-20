package com.braumsolutions.advogadoresponde.View;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.Model.CasesModel;
import com.braumsolutions.advogadoresponde.Model.YourCasesAdapter;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class YourCasesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView lvCase;
    private FirebaseAuth mAuth;
    private ArrayAdapter<CasesModel> adapter;
    private ArrayList<CasesModel> arrayList;
    private ValueEventListener eventListener;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_cases);

        mAuth = FirebaseAuth.getInstance();

        castWidgets();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.your_cases));
        getSupportActionBar().setSubtitle(getString(R.string.your_cases_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getCases();

    }

    private void getCases() {
        arrayList = new ArrayList<>();
        adapter = new YourCasesAdapter(getApplicationContext(), arrayList);
        lvCase.setAdapter(adapter);

        database = FirebaseUtils.getDatabase().getReference().child(CASES);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    //MENSAGEM QUE NAO HA CASOS
                } else {
                    for (DataSnapshot cases : dataSnapshot.getChildren()) {
                        if (cases.child(USER).getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                            CasesModel c = cases.getValue(CasesModel.class);
                            arrayList.add(c);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        lvCase = findViewById(R.id.lvCase);
    }

    @Override
    protected void onResume() {
        try {
            database.addValueEventListener(eventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            database.removeEventListener(eventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

}
