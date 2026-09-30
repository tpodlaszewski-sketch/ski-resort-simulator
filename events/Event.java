package events;

public abstract class Event {
    private int time;

    public Event(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public abstract void execute();

    protected String formatTime() {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}