//Simulator.java
/**
 * A general purpose simulation framework
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 */

import java.util.PriorityQueue;

class Simulator {

    private static PriorityQueue<Event> eventSet
            = new PriorityQueue<Event>(
            (Event e1, Event e2) -> Float.compare(e1.time, e2.time)
    );

    static boolean moreEvents() {
        /** Report if there pending and not yet triggered events.
         */
        return !eventSet.isEmpty();
    }

    static void schedule(float time, Action act) {
        /** Call schedule(time,act) to make act happen at time.
         *  Users typically pass the action as a lambda expression:
         *  <PRE>
         *  Simulator.schedule(t,(float time)->method(params,time))
         *  </PRE>
         */
        Event e = new Event();
        e.time = time;
        e.act = act;
        eventSet.add(e);
    }

    static void run() {
        /** Call run() after scheduling some initial events
         *  to run the simulation.
         */
        while (!eventSet.isEmpty()) {
            Event e = eventSet.remove();
            e.act.trigger(e.time);
        }
    }

    public interface Action {
        // actions contain the specific code of each event
        // users generally use lambda abstraction
        // so they don't usually see this class.
        void trigger(float time);
    }

    private static class Event {
        public float time; // the time of this event
        public Action act; // what to do at that time
    }
}