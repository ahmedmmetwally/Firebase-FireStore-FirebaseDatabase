package com.example.firebaseuploadexample2;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageView> {
    @BindView(R.id.text_viwe_name)
    TextView textViweName;
    @BindView(R.id.image_view_upload)
    android.widget.ImageView imageViewUpload;
    private Context context;
    private List<UpLoad> mUpload;
    private NewLlistener mNewLlistener;

    public ImageAdapter(Context context, List<UpLoad> mUpload) {
        this.context = context;
        this.mUpload = mUpload;
    }

    @NonNull
    @Override
    public ImageView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageView holder, int position) {
        UpLoad upLoadCurrent = mUpload.get(position);
        holder.textViweName.setText(upLoadCurrent.getmName());
        Picasso.with(context).load(upLoadCurrent.getmImageUrl()).placeholder(R.mipmap.ic_launcher)
                .fit().centerCrop().into(holder.imageViewUpload);
    }

    @Override
    public int getItemCount() {
        return mUpload.size();
    }


    public class ImageView extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener,MenuItem.OnMenuItemClickListener{
        @BindView(R.id.text_viwe_name)
        TextView textViweName;
        @BindView(R.id.image_view_upload)
        android.widget.ImageView imageViewUpload;
        private View view;

        public ImageView(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mNewLlistener!=null){
            int postion=getAdapterPosition();
            if(postion!=RecyclerView.NO_POSITION)
            mNewLlistener.onItemClick(postion);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem do_whatever=menu.add(Menu.NONE,1,1,"Do Whatever");
            MenuItem delete=menu.add(Menu.NONE,2,2,"Delete");

            do_whatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mNewLlistener!=null){
               int postion=getAdapterPosition();
               if(postion!=RecyclerView.NO_POSITION){
                   switch (item.getItemId()){
                   case 1:
                       mNewLlistener.onWhatEverClick(postion);
                       return true;
                       case 2:
                           mNewLlistener.onDeleteClick(postion);
                           return true;
               }}
           }
            return false;
        }
    }
    public interface NewLlistener{
        public void onItemClick(int position);
        void onWhatEverClick(int position);
        void onDeleteClick(int position);
    }
    public void setNewListener(NewLlistener newListener){
        this.mNewLlistener=newListener;
    }
}
