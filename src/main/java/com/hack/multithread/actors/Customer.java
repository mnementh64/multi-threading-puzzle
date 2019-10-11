package com.hack.multithread.actors;

import com.hack.multithread.office.PostOffice;

import java.time.Duration;
import java.util.Random;

public class Customer extends SimulationActor {

    public static final int DOING_NOTHING = 0;
    public static final int QUEUING = 1;
    public static final int SITTING = 2;
    public static final int POSTING = 3;

    private static final Random random = new Random(System.nanoTime());

    private final PostOffice postOffice;
    private Duration durationBeforePostingAgain;
    private boolean okToRun = true;

    public Customer(String name, PostOffice postOffice) {
        super(Customer.DOING_NOTHING, "Customer " + name);
        this.postOffice = postOffice;
        prepareNextPosting();
    }

    @Override
    public void run() {
        while (okToRun) {
            if (status == DOING_NOTHING) {
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
        setStatus(DOING_NOTHING);

        // compute waiting time until next posting
        durationBeforePostingAgain = Duration.ofSeconds(2 + random.nextInt(8));
    }

    protected String statusAsPrettyText(int status) {
        switch (status) {
            case DOING_NOTHING:
                return "doing nothing";
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
