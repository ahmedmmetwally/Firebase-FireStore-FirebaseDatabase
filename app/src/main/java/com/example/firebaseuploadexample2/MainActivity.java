package com.example.firebaseuploadexample2;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button_choose_image)
    Button buttonChooseImage;
    @BindView(R.id.edit_text_file_name)
    EditText editTextFileName;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.progress_par)
    ProgressBar progressPar;
    @BindView(R.id.button_upload)
    Button buttonUpload;
    @BindView(R.id.text_view_show_uploads)
    TextView textViewShowUploads;
    private Uri mImageUri;
    Unbinder unbinder;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    public static final int PICK_IMAGE_RESOURCE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        mStorageRef= FirebaseStorage.getInstance().getReference("Upload");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("upload");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.button_choose_image, R.id.button_upload, R.id.text_view_show_uploads})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_choose_image:
                openFileChooser();
                break;
            case R.id.button_upload:
                if(mUploadTask!=null&&mUploadTask.isInProgress())
                    Toast.makeText(MainActivity.this,"Upload in progress",Toast.LENGTH_LONG).show();
                UploadFile();
                break;

            case R.id.text_view_show_uploads:
                openImagesActivity();
                break;
        }
    }

    private void openImagesActivity() {
        Intent intent=new Intent(this,ImagesActivity.class);
        startActivity(intent);
    }

    public String getFileExtention(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void UploadFile() {
        if(mImageUri !=null){
            StorageReference fileRefrence = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtention(mImageUri));
            mUploadTask= fileRefrence.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressPar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(getApplicationContext(), "upLoad is successfully...", Toast.LENGTH_LONG).show();
                            UpLoad upload=new UpLoad(editTextFileName.getText().toString().trim(),taskSnapshot.getMetadata().toString());
                            String uploadId=mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressPar.setProgress((int) progress);

                        }
                    });
        }else
            Toast.makeText(this, "NO File Selected", Toast.LENGTH_LONG).show();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_RESOURCE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_RESOURCE && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(imageView);
        }
    }
}
