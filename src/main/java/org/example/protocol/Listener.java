package org.example.protocol;

public abstract class Listener implements Runnable {
    protected boolean isRunning=true;
    @Override
    public void run() {
        listen();
    }
    abstract void listen();

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
