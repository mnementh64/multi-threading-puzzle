package com.hack.multithread.actors;

import com.hack.multithread.office.PostOffice;

import java.time.Duration;
import java.util.Random;

public class PostCar extends SimulationActor {

    public static final int WAITING_FOR_PARCEL = 0;
    public static final int QUEUING = 1;
    public static final int LOADED = 2;
    public static final int DELIVERING = 3;

    private static final Random random = new Random(System.nanoTime());

    private final PostOffice postOffice;
    private Duration deliveryDuration;
    private boolean okToRun = true;

    public PostCar(String name, PostOffice postOffice) {
        super(PostCar.WAITING_FOR_PARCEL, "Post car " + name);
        this.postOffice = postOffice;
        startDelivering();
    }

    @Override
    public void run() {
        while (okToRun) {
            if (status == DELIVERING) {
                try {
                    System.out.println(System.nanoTime() + " :     " + getName() + " is back to the office in  " + deliveryDuration.getSeconds() + "s");
                    sleep(deliveryDuration.toMillis());

                    System.out.println(System.nanoTime() + " :     " + getName() + " is back to the office");
                    setStatus(QUEUING);
                } catch (InterruptedException e) {
                    // to ensure thread lifecycle
                    Thread.currentThread().interrupt();
                    okToRun = false;
                    return;
                }
            }

            if (status == LOADED) {
                startDelivering();
            }

            if (status == QUEUING) {
                postOffice.tryToPark(this);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                okToRun = false;
                return;
            }
        }
    }

    private void startDelivering() {
        setStatus(DELIVERING);

        // compute delivering time
        deliveryDuration = Duration.ofSeconds(2 + random.nextInt(8));
    }

    @Override
    public void setStatus(int status) {
        System.out.println(System.nanoTime() + " :     " + getName() + " : is " + statusAsPrettyText(status));
        super.setStatus(status);
    }

    protected String statusAsPrettyText(int status) {
        switch (status) {
            case WAITING_FOR_PARCEL:
                return "waiting for parcel";
            case QUEUING:
                return "queuing";
            case LOADED:
                return "loaded";
            case DELIVERING:
                return "delivering";
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    }
}
