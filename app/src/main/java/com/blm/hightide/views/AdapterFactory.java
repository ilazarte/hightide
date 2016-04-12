package com.blm.hightide.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;

public class AdapterFactory<T> {

    private Context context;

    private Binder<T> sourceBinder;

    private int layout;

    public AdapterFactory(Context context, Binder<T> sourceBinder, int layout) {
        this.context = context;
        this.sourceBinder = sourceBinder;
        this.layout = layout;
    }

    public class Holder extends RecyclerView.ViewHolder {

        private Binder<T> binder;

        public Holder(View view) {
            super(view);
            binder = sourceBinder.create();
            ButterKnife.bind(binder, view);
        }

        public void bind(T item, int position) {
            binder.bind(item, position);
        }
    }

    public class Adapter extends RecyclerView.Adapter<Holder> {

        private List<T> items;

        public Adapter(List<T> items) {
            this.items = items;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(AdapterFactory.this.context);
            View view = inflator.inflate(layout, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            T item = items.get(position);
            holder.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public Adapter adapter(List<T> items) {
        return new Adapter(items);
    }
}
