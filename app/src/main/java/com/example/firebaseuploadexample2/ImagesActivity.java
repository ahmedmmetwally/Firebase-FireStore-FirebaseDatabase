package com.example.firebaseuploadexample2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.NewLlistener {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_circl)
    ContentLoadingProgressBar progressCircl;
    private FirebaseStorage mfFirebaseStorage;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mDBListener;
    private ImageAdapter mImageAdapter;
    private List<UpLoad> mUpLoads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("recycler");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUpLoads = new ArrayList<>();
        mImageAdapter = new ImageAdapter(ImagesActivity.this, mUpLoads);
        mRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.setNewListener(ImagesActivity.this);
        mfFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("upload");
        mDBListener = mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUpLoads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UpLoad upLoad = postSnapshot.getValue(UpLoad.class);
                    upLoad.setmKey(dataSnapshot.getKey());
                    mUpLoads.add(upLoad);
                }
                mImageAdapter.notifyDataSetChanged();
                progressCircl.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                progressCircl.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(ImagesActivity.this, "normal click at position :" + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(ImagesActivity.this, "whatever click at position :" + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteClick(int position) {
        UpLoad upLoad = mUpLoads.get(position);
        String key = upLoad.getmKey();
        StorageReference msStorageReference = mfFirebaseStorage.getReferenceFromUrl(upLoad.getmImageUrl());
        msStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseReference.child(key).removeValue();

                Toast.makeText(ImagesActivity.this, "item deleted", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mDBListener);
    }
}
