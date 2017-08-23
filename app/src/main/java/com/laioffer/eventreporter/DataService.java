package com.laioffer.eventreporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by program on 6/28/2017.
 */

public class DataService {
    /**
     * Fake all the event data for now. We will refine this and connect
     * to our backend later.
     */
    public static List<Event> getEventData() {
        List<Event> eventData = new ArrayList<Event>();//a list, the element stored in it is Event type, which has been defined in the Class:Event
        for (int i = 0; i < 10; ++i) {
            eventData.add(
                    new Event("Event", "1184 W valley Blvd, CA 90101",
                            "This is a huge event"));
        }
        return eventData;
    }
}


