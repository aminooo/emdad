package com.upsym.emdad.earthquake.main.events;

import com.upsym.emdad.earthquake.database.EventPoint;

import java.util.List;

/**
 * Created by Karo on 11/16/2017.
 */

public class UpdatedEvent {
    List<EventPoint> eventPoints;
    public UpdatedEvent(List<EventPoint> eventPoints) {
        this.eventPoints = eventPoints;
    }

    public List<EventPoint> getEventPoints() {
        return eventPoints;
    }
}
