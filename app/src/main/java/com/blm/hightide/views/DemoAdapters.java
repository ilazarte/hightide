package com.blm.hightide.views;

import com.blm.corals.Tick;
import com.blm.hightide.R;

import java.util.ArrayList;

public class DemoAdapters {
    public static void main(String[] args) {

        AdapterFactory<Tick> factory = new AdapterFactory<>(null, new TickBinder(), R.layout.list_item_daily_tick);
        AdapterFactory<Tick>.Adapter adapter = factory.adapter(new ArrayList<>());
    }
}
