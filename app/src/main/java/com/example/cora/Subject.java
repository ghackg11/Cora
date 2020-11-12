package com.example.cora;

public class Subject implements Comparable{

    public String time;
    public boolean bunk;
    public boolean cancelled;
    public String symbol;
    public int period;

    public Subject(String time, boolean bunk, boolean cancelled, String symbol, int period) {
        this.time = time;
        this.bunk = bunk;
        this.cancelled = cancelled;
        this.symbol = symbol;
        this.period = period;
    }

    public Subject(){};

    @Override
    public int compareTo(Object o) {
        Subject s = (Subject) o;

        if(s.period>this.period) return -1;
        if(s.period==this.period) return 0;
        return 1;

    }

}
