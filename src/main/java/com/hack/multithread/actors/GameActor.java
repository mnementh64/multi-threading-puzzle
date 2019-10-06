package com.hack.multithread.actors;

public abstract class GameActor extends Thread {

    int status;

    GameActor(int status, String name) {
        super();
        this.status = status;
        setName(name);
    }

    protected abstract String statusAsPrettyText(int status);

    public void setStatus(int status) {
        System.out.println(System.nanoTime() + " : " + getName() + " : is " + statusAsPrettyText(status));
        this.status = status;
    }
}
