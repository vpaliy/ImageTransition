package com.example.vasya.phototransition;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.imagetransition.TransitionStarter;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG=MainActivity.class.getSimpleName();

    private static final int DATA_REQUEST=1;
    private boolean isPreLollipop=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestFeature();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},DATA_REQUEST);
            }else {
                makeQuery();
            }
        }else {
            makeQuery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case DATA_REQUEST:
                makeQuery();
        }
    }

    private void makeQuery() {
        new AsyncTask<Void,Void,ArrayList<File>>() {
            @Override
            protected ArrayList<File> doInBackground(Void... voids) {
                Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Images.ImageColumns.DATA},null,null,null);
                List<File> imageFileList=new LinkedList<>();    //for better performance
                if(cursor!=null) {
                    if(cursor.moveToFirst()) {
                        do {
                            imageFileList.add(new File(cursor.getString
                            (cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
                return new ArrayList<>(imageFileList);
            }

            @Override
            protected void onPostExecute(ArrayList<File> files) {
                super.onPostExecute(files);
                RecyclerView recyclerView=(RecyclerView)(findViewById(R.id.recyclerView));
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,4,GridLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(new GalleryAdapter(MainActivity.this,files));
                final DrawerLayout layout=(DrawerLayout)(findViewById(R.id.drawerLayout));
                NavigationView navigationView=(NavigationView)(findViewById(R.id.navigation));
                navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.lollipop:
                                isPreLollipop=false;
                                break;
                            case R.id.pre_lollipop:
                                isPreLollipop=true;
                                break;
                        }
                        layout.closeDrawers();
                        return true;
                    }
                });
            }
        }.execute(null,null);
    }

    private class GalleryAdapter extends
            RecyclerView.Adapter<GalleryAdapter.ImageViewHolder> {

        private ArrayList<File> mediaFileList;
        private LayoutInflater inflater;

        public GalleryAdapter(Context context, ArrayList<File> mediaFileList) {
            this.inflater=LayoutInflater.from(context);
            this.mediaFileList=mediaFileList;
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            private DynamicImage image;

            public ImageViewHolder(View itemView) {
                super(itemView);
                image=(DynamicImage)(itemView);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    image.setTransitionName("thumbnail");
                }
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isPreLollipop) {
                            makePreLollipopTransition(mediaFileList.get(getAdapterPosition()), image);
                        }else {
                            makeLollipopTransition(mediaFileList.get(getAdapterPosition()),image);
                        }
                    }
                });
            }

            public void onBindData() {
                Glide.with(itemView.getContext())
                        .load(mediaFileList.get(getAdapterPosition()))
                        .asBitmap()
                        .centerCrop()
                        .into(image);
            }
        }



        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root=inflater.inflate(R.layout.image,parent,false);

            return new ImageViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.onBindData();
        }

        @Override
        public int getItemCount() {
            return mediaFileList.size();
        }
    }

    private void makePreLollipopTransition(@NonNull File mediaFile, ImageView image) {

        Intent intent=new Intent(this,DetailActivity.class);
        intent.putExtra("key",mediaFile.getAbsolutePath());
        TransitionStarter.with(this).from(image).start(intent); //that's it!

    }


    @TargetApi(21)
    private void makeLollipopTransition(@NonNull File mediaFile,ImageView image) {
        Intent intent=new Intent(this,LollipopActivity.class);
        intent.putExtra("key",mediaFile.getAbsolutePath());
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation(this, image, image.getTransitionName());
        startActivity(intent, options.toBundle());

    }

    @TargetApi(21)
    private void requestFeature() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setSharedElementsUseOverlay(false);
    }

}
