package com.blm.hightide.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class RecyclerViewAdapterFactory<T> {

    private Context context;

    private int layoutResource;

    public RecyclerViewAdapterFactory() {
    }

    /**
     * Create the factory.
     * @param context Usually an activity
     * @param layoutResource The layout resource to use for rendering.
     */
    public RecyclerViewAdapterFactory(Context context, int layoutResource) {
        this.context = context;
        this.layoutResource = layoutResource;
    }

    /**
     * Used to initialize bindings
     * @param view The root view of the line item.
     */
    public abstract void create(View view);

    /**
     * Used to set the data to the recycled holder.
     * @param item
     */
    public abstract void bind(T item, int position);

    /**
     * Produce a new adapter (used to reset data)
     * @param items
     * @return
     */
    public Adapter adapter(List<T> items) {
        return new Adapter(items);
    }

    class Holder extends RecyclerView.ViewHolder {
        public Holder(View view) {
            super(view);
            RecyclerViewAdapterFactory.this.create(view);
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        private List<T> items;

        public Adapter(List<T> items) {
            this.items = items;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(context);
            View view = inflator.inflate(layoutResource, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            T item = items.get(position);
            RecyclerViewAdapterFactory.this.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
