package com.hack.multithread.office;

import com.hack.multithread.actors.Clerk;
import com.hack.multithread.actors.Customer;
import com.hack.multithread.actors.PostCar;

public class PostOffice extends Thread {

    private final FrontOffice frontOffice;
    private final BackOffice backOffice;

    private boolean okToRun = true;

    public PostOffice(Clerk clerk, int nbSeats, int nbCars) {
        this.frontOffice = new FrontOffice(clerk, nbSeats);
        this.backOffice = new BackOffice(clerk, nbCars);
    }

    @Override
    public synchronized void start() {
        System.out.println(System.nanoTime() + " : Post office is opened !");
        super.start();
    }

    @Override
    public void run() {
        // Post office forever open
        while (okToRun) {
            // if all seats are occupied / all cars are parked, awake the clerk
            frontOffice.maybeAwakeClerkForCustomers();
            backOffice.maybeAwakeClerkForCars();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                okToRun = false;
                Thread.currentThread().interrupt();
                System.out.println(System.nanoTime() + " : Post office is closed !");
                return;
            }
        }

        System.out.println(System.nanoTime() + " : Post office is closed !");
    }

    public void tryToSeat(Customer newCustomer) {
        frontOffice.tryToSeat(newCustomer);
    }

    public void tryToPark(PostCar newPostCar) {
        backOffice.tryToPark(newPostCar);
    }
}
