package com.braumsolutions.advogadoresponde.View.Cases;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.braumsolutions.advogadoresponde.Model.CasesAdapter;
import com.braumsolutions.advogadoresponde.Model.CasesModel;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class CasesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView lvCase;
    private FirebaseAuth mAuth;
    private ArrayAdapter<CasesModel> adapter;
    private ArrayList<CasesModel> arrayList;
    private ValueEventListener eventListener;
    private DatabaseReference database;
    private KProgressHUD dialog;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cases);

        mAuth = FirebaseAuth.getInstance();

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        castWidgets();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.find_cases));
        getSupportActionBar().setSubtitle(getString(R.string.find_cases_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getCases();
        getUserData();

        lvCase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CasesModel casesModel = arrayList.get(position);
                Intent intentCase = new Intent(getApplicationContext(), OpenCaseActivity.class);
                intentCase.putExtra(KEY, casesModel.getKey());
                intentCase.putExtra(USER, casesModel.getUser());
                intentCase.putExtra(TYPE_REGISTER, type);
                startActivity(intentCase);
            }
        });

    }

    private void getUserData() {
        DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(USER).child(mAuth.getCurrentUser().getUid());
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                type = dataSnapshot.child(TYPE_REGISTER).getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(CasesActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
    }

    private void getCases() {
        arrayList = new ArrayList<>();
        adapter = new CasesAdapter(getApplicationContext(), arrayList);
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
                        CasesModel c = cases.getValue(CasesModel.class);
                        arrayList.add(c);
                    }
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
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
