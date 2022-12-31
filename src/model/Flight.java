package model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Flight {
    public final DayOfWeek dayOfWeek;
    public final String departureTime;
    public final int departureDelay;
    public final String arrivalTime;
    public final int arrivalDelay;
    public final int duration;
    public final int distance;
    public final boolean cancelled;
    public final boolean diverted;

    public Flight(DayOfWeek dayOfWeek, LocalTime departureTime, int departureDelay, LocalTime arrivalTime, int arrivalDelay, int duration, int distance, boolean cancelled, boolean diverted) {
        this.dayOfWeek = dayOfWeek;
        this.departureTime = departureTime.toString();
        this.departureDelay = departureDelay;
        this.arrivalTime = arrivalTime.toString();
        this.arrivalDelay = arrivalDelay;
        this.duration = duration;
        this.distance = distance;
        this.cancelled = cancelled;
        this.diverted = diverted;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public int getDepartureDelay() {
        return departureDelay;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getArrivalDelay() {
        return arrivalDelay;
    }

    public int getDuration() {
        return duration;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDiverted() {
        return diverted;
    }

    

    @Override
    public String toString() {
        return "Flight{" + "dayOfWeek=" + dayOfWeek + ", departureTime=" + departureTime + ", departureDelay=" + departureDelay + ", arrivalTime=" + arrivalTime + ", arrivalDelay=" + arrivalDelay + ", duration=" + duration + ", distance=" + distance + ", cancelled=" + cancelled + ", diverted=" + diverted + '}';
    }

   

}
