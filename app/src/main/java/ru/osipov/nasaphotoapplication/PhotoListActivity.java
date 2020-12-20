package ru.osipov.nasaphotoapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import ru.osipov.nasaphotoapplication.api.model.PhotoDTO;

public class PhotoListActivity extends AppCompatActivity {

    private static final String EXTRA_DATE = "PhotoListActivity.EXTRA_DATE";

    CompositeDisposable disposable = new CompositeDisposable();

    RecyclerView recyclerView;
    Adapter adapter;

    public static void start(Context caller, String date){
        Intent intent = new Intent(caller, PhotoListActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list);

        adapter = new Adapter();

        getSupportActionBar().setTitle(getString(R.string.choose_time));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        App app = (App) getApplication();

        disposable.add(app.getNasaService().getApi().getPhotosForDate(getIntent().getStringExtra(EXTRA_DATE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<PhotoDTO>, Throwable>() {
                    @Override
                    public void accept(List<PhotoDTO> photoDTOS, Throwable throwable) throws Exception {
                        if (throwable != null){
                            Toast.makeText(PhotoListActivity.this, "Data loading error", Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.setPhotos(photoDTOS);
                        }
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private static class Adapter extends RecyclerView.Adapter<PhotoItemViewHolder>{

        private ArrayList<PhotoDTO> photos = new ArrayList<>();

        public void setPhotos(List<PhotoDTO> photos){
            this.photos.clear();
            this.photos.addAll(photos);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PhotoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PhotoItemViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_photo, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoItemViewHolder holder, int position) {
            holder.bind(photos.get(position));
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }

    private static class PhotoItemViewHolder extends RecyclerView.ViewHolder{

        TextView text;
        PhotoDTO photo;

        public PhotoItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoActivity.start(v.getContext(), photo.getImageUrl());
                }
            });
        }

        public void bind(PhotoDTO photo){
            text.setText(photo.getDate());
            this.photo = photo;
        }
    }
}
