package events;

public class EventWrapper implements Comparable<EventWrapper> {
    private final Event event;
    private final long sequenceNumber;

    public EventWrapper(Event event, long sequenceNumber) {
        this.event = event;
        this.sequenceNumber = sequenceNumber;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public int compareTo(EventWrapper other) {
        // First, compare by execution time
        int timeComparison = Integer.compare(this.event.getTime(), other.event.getTime());
        if (timeComparison != 0) {
            return timeComparison;
        }
        // If times are equal, the event inserted earlier executes first
        return Long.compare(this.sequenceNumber, other.sequenceNumber);
    }
}