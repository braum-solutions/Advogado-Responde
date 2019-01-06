package com.braumsolutions.advogadoresponde.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.Model.CasesAdapter;
import com.braumsolutions.advogadoresponde.Model.CasesModel;
import com.braumsolutions.advogadoresponde.Model.LawyerCasesAdapter;
import com.braumsolutions.advogadoresponde.Model.LawyerCasesModel;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;

import static com.braumsolutions.advogadoresponde.Utils.TypefaceUtils.TypefaceLight;
import static com.braumsolutions.advogadoresponde.Utils.Utils.CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.KEY;
import static com.braumsolutions.advogadoresponde.Utils.Utils.LAWYER_CASES;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class LawyerCasesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView lvCase;
    private TextView tvNoCases;
    private FirebaseAuth mAuth;
    private ArrayAdapter<LawyerCasesModel> adapter;
    private ArrayList<LawyerCasesModel> arrayList;
    private ValueEventListener eventListener;
    private DatabaseReference database;
    private KProgressHUD dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_cases);

        mAuth = FirebaseAuth.getInstance();

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        castWidgets();
        setTypeface();
        getCases();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.your_cases));
        getSupportActionBar().setSubtitle(getString(R.string.your_cases_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        lvCase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LawyerCasesModel lawyerCasesModel = arrayList.get(position);
                DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CASES).child(lawyerCasesModel.getCases());
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intentCase = new Intent(getApplicationContext(), OpenCaseActivity.class);
                        intentCase.putExtra(KEY, dataSnapshot.child(KEY).getValue(String.class));
                        intentCase.putExtra(USER, dataSnapshot.child(USER).getValue(String.class));
                        startActivity(intentCase);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void createDialog(String title, String message) {
        dialog = KProgressHUD.create(LawyerCasesActivity.this)
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
        adapter = new LawyerCasesAdapter(getApplicationContext(), arrayList);
        lvCase.setAdapter(adapter);

        database = FirebaseUtils.getDatabase().getReference().child(LAWYER_CASES).child(mAuth.getCurrentUser().getUid());
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNoCases.setVisibility(View.VISIBLE);
                } else {
                    tvNoCases.setVisibility(View.GONE);
                    for (DataSnapshot cases : dataSnapshot.getChildren()) {
                        LawyerCasesModel c = cases.getValue(LawyerCasesModel.class);
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

    private void setTypeface() {
        tvNoCases.setTypeface(TypefaceLight(getApplicationContext()));
    }

    private void castWidgets() {
        toolbar = findViewById(R.id.toolbar);
        lvCase = findViewById(R.id.lvCase);
        tvNoCases = findViewById(R.id.tvNoCases);
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

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    } */
}
