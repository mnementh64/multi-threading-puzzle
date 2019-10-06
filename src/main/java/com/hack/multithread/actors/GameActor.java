package com.hack.multithread.actors;

public abstract class GameActor extends Thread {

    protected int status;

    protected GameActor(int status, String name) {
        super();
        this.status = status;
        setName(name);
    }

    protected abstract String statusAsPrettyText(int status);

    public void setStatus(int status) {
        System.out.println(getName() + " : is " + statusAsPrettyText(status));
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
