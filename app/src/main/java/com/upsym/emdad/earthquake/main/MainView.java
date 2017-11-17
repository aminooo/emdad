package com.upsym.emdad.earthquake.main;

import com.upsym.emdad.earthquake.database.EventPoint;

import java.util.List;

/**
 * Created by Karo on 11/16/2017.
 */

public interface MainView {
    void openNewEventDialog();

    void reloadMap(List<EventPoint> markerData);
}
