package com.blm.hightide.views;

public interface Binder <T> {

    /**
     * Used to create instances of the bound object without reflection.
     * @return An instance of itself.
     */
    Binder<T> create();

    /**
     * Bind the object to the views available.
     * @param item The object
     * @param position The location in the collection.
     */
    void bind(T item, int position);
}
