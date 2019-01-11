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
import android.widget.TextView;
import android.widget.Toast;

import com.braumsolutions.advogadoresponde.Model.CasesModel;
import com.braumsolutions.advogadoresponde.Model.YourCasesAdapter;
import com.braumsolutions.advogadoresponde.R;
import com.braumsolutions.advogadoresponde.Utils.FirebaseUtils;
import com.chootdev.csnackbar.Snackbar;
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
import static com.braumsolutions.advogadoresponde.Utils.Utils.TYPE_REGISTER;
import static com.braumsolutions.advogadoresponde.Utils.Utils.USER;

public class UserCasesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView lvCase;
    private TextView tvNoCases;
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
        setContentView(R.layout.activity_user_cases);

        mAuth = FirebaseAuth.getInstance();

        createDialog(getString(R.string.please_wait), getString(R.string.loading));

        castWidgets();
        setTypeface();
        getUserData();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.your_cases));
        getSupportActionBar().setSubtitle(getString(R.string.your_cases_msg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getCases();

        lvCase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CasesModel CasesModel = arrayList.get(position);
                DatabaseReference database = FirebaseUtils.getDatabase().getReference().child(CASES).child(CasesModel.getKey());
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intentCase = new Intent(getApplicationContext(), OpenCaseActivity.class);
                        intentCase.putExtra(KEY, dataSnapshot.child(KEY).getValue(String.class));
                        intentCase.putExtra(TYPE_REGISTER, type);
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
        dialog = KProgressHUD.create(UserCasesActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(title)
                .setDetailsLabel(message)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        dialog.show();
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

    private void setTypeface() {
        tvNoCases.setTypeface(TypefaceLight(getApplicationContext()));
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
                    tvNoCases.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot cases : dataSnapshot.getChildren()) {
                        if (cases.child(USER).getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
                            tvNoCases.setVisibility(View.GONE);
                            CasesModel c = cases.getValue(CasesModel.class);
                            arrayList.add(c);
                        }
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

}
