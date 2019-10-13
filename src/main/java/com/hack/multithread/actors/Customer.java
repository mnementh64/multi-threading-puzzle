package com.hack.multithread.actors;

import com.hack.multithread.office.PostOffice;
import com.hack.multithread.util.SimpleRandom;

public class Customer extends SimulationActor {

    public static final int BACK_TO_HOME = 0;
    public static final int QUEUING = 1;
    public static final int SITTING = 2;
    public static final int POSTING = 3;

    private static final SimpleRandom timeAtHomeRandom = new SimpleRandom(8);
    private static final SimpleRandom nbParcelsRandom = new SimpleRandom(2);

    private final PostOffice postOffice;
    private long millisBeforePostingAgain;
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
                    System.out.println(System.nanoTime() + " : " + getName() + " is waiting " + (millisBeforePostingAgain / 1000) + "s before go back to the post-office");
                    sleep(millisBeforePostingAgain);
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
        millisBeforePostingAgain = 2000 + timeAtHomeRandom.nextInt() * 1000; // should have used Duration here but ... only java.lang !
        nbParcels = nbParcelsRandom.nextInt() + 1;
    }

    public int getNbParcels() {
        return nbParcels;
    }

    @Override
    public void setStatus(int status) {
        System.out.println(System.nanoTime() + " : " + getName() + " : is " + statusAsPrettyText(status));
        super.setStatus(status);
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
