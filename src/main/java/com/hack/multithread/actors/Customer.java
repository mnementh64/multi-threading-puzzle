package com.hack.multithread.actors;

import com.hack.multithread.office.PostOffice;

import java.time.Duration;
import java.util.Random;

public class Customer extends SimulationActor {

    public static final int BACK_TO_HOME = 0;
    public static final int QUEUING = 1;
    public static final int SITTING = 2;
    public static final int POSTING = 3;

    private static final Random random = new Random(System.nanoTime());

    private final PostOffice postOffice;
    private Duration durationBeforePostingAgain;
    private boolean okToRun = true;
    private int nbParcels;

    public Customer(String name, PostOffice postOffice) {
        super(Customer.BACK_TO_HOME, "Customer " + name);
        this.postOffice = postOffice;
        prepareNextPosting();
    }

    @Override
    public void run() {
        while (okToRun) {
            if (status == BACK_TO_HOME) {
                try {
                    System.out.println(System.nanoTime() + " : " + getName() + " is waiting " + durationBeforePostingAgain.getSeconds() + "s before go back to the post-office");
                    sleep(durationBeforePostingAgain.toMillis());
                    setStatus(QUEUING);
                } catch (InterruptedException e) {
                    // to ensure thread lifecycle
                    Thread.currentThread().interrupt();
                    okToRun = false;
                    return;
                }
            }

            if (status != SITTING && status != POSTING) {
                // go again to the post office
                tryToSeat();
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

    private void tryToSeat() {
        // try to seat in the post office --> blocking call
        postOffice.tryToSeat(this);
    }

    public void prepareNextPosting() {
        setStatus(BACK_TO_HOME);

        // compute waiting time until next posting
        durationBeforePostingAgain = Duration.ofSeconds(2 + random.nextInt(8));
        nbParcels = random.nextInt(2) + 1;
    }

    public int getNbParcels() {
        return nbParcels;
    }

    protected String statusAsPrettyText(int status) {
        switch (status) {
            case BACK_TO_HOME:
                return "back to home";
            case QUEUING:
                return "queuing";
            case SITTING:
                return "sitting";
            case POSTING:
                return "posting";
            default:
                throw new IllegalStateException("Unexpected value: " + status);
        }
    }
}
