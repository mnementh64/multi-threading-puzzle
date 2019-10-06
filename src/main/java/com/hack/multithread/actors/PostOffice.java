package com.hack.multithread.actors;

import java.util.NoSuchElementException;

public class PostOffice extends Thread {

    private final Object seatMutex = new Object();
    private Customer[] customersSit;
    private final int nbSeats;

    private final Object carMutex = new Object();
    private PostCar[] postCarsWaiting;
    private final int nbCars;

    private final Object clerkMutex = new Object();

    private boolean okToRun = true;

    public PostOffice(int nbSeats, int nbCars) {
        this.nbSeats = nbSeats;
        this.customersSit = new Customer[nbSeats]; // no customer at the beginning
        this.nbCars = nbCars;
        this.postCarsWaiting = new PostCar[nbCars]; // no car at the post office at the beginning
    }

    @Override
    public synchronized void start() {
        System.out.println(System.currentTimeMillis() + " : Post office is opened !");
        super.start();
    }

    @Override
    public void run() {
        // Post office forever open
        while (okToRun) {
            // if all seats are occupied / all cars are parked, awake the clerk
            maybeAwakeClerk();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                okToRun = false;
                Thread.currentThread().interrupt();
                System.out.println(System.currentTimeMillis() + " : Post office is closed !");
                return;
            }
        }

        System.out.println(System.currentTimeMillis() + " : Post office is closed !");
    }

    void tryToSeat(Customer newCustomer) {
        synchronized (seatMutex) {
            try {
                // go on queuing if all seats are already occupied
                if (nbSeatsOccupied() == nbSeats) {
                    return;
                }

                // a seat is available - just occupy it
                customersSit[firstFreeIndex(customersSit)] = newCustomer;
                newCustomer.setStatus(Customer.SITTING);

                maybeAwakeClerkForCustomers();
            } finally {
                seatMutex.notifyAll();
            }
        }
    }

    void tryToPark(PostCar newPostCar) {
        synchronized (carMutex) {
            try {
                // a place should available - just occupy it
                postCarsWaiting[firstFreeIndex(postCarsWaiting)] = newPostCar;
                newPostCar.setStatus(PostCar.WAITING_FOR_PARCEL);

                maybeAwakeClerkForCars();
            } finally {
                carMutex.notifyAll();
            }
        }
    }

    private void maybeAwakeClerk() {
        maybeAwakeClerkForCustomers();
        maybeAwakeClerkForCars();
    }

    private void maybeAwakeClerkForCustomers() {
        synchronized (seatMutex) {
            try {
                // all seats occupied ? Awake the clerk !
                if (nbSeatsOccupied() == nbSeats) {
                    synchronized (clerkMutex) {
                        try {
                            System.out.println(System.currentTimeMillis() + " : Clerk is awaken ! Back to work for customers.");
                            for (Customer customer : customersSit) {
                                customer.setStatus(Customer.POSTING);
                                customer.prepareNextPosting();
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }

                            // erase all sit customers --> seats are free again
                            for (int i = 0; i < nbSeats; i++) {
                                customersSit[i] = null;
                            }
                            System.out.println(System.currentTimeMillis() + " : Clerk is sleeping again after a hard work with customers.");
                        } finally {
                            clerkMutex.notify();
                        }
                    }
                }
            } finally {
                seatMutex.notifyAll();
            }
        }
    }

    private void maybeAwakeClerkForCars() {
        synchronized (carMutex) {
            try {
                // all post cars at the office ? Awake the clerk !
                if (nbCarsAtTheOffice() == nbCars) {
                    synchronized (clerkMutex) {
                        try {
                            System.out.println(System.currentTimeMillis() + " : Clerk is awaken ! Back to work for post cars.");
                            for (PostCar postCar : postCarsWaiting) {
                                postCar.setStatus(PostCar.LOADED);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }

                            // erase all post cars waiting --> parking places are free again
                            for (int i = 0; i < nbCars; i++) {
                                postCarsWaiting[i] = null;
                            }
                            System.out.println(System.currentTimeMillis() + " : Clerk is sleeping again after a hard work with post cars.");
                        } finally {
                            clerkMutex.notifyAll();
                        }
                    }
                }
            } finally {
                carMutex.notifyAll();
            }
        }
    }

    private int firstFreeIndex(Object[] arrayOfValues) {
        for (int i = 0; i < arrayOfValues.length; i++) {
            if (arrayOfValues[i] == null) {
                return i;
            }
        }
        throw new NoSuchElementException("No free index in this array of values (size " + arrayOfValues.length + ")");
    }

    private int nbSeatsOccupied() {
        synchronized (seatMutex) {
            int nb = 0;
            for (Customer customerName : customersSit) {
                if (customerName != null) {
                    nb++;
                }
            }

            return nb;
        }
    }

    private int nbCarsAtTheOffice() {
        synchronized (carMutex) {
            int nb = 0;
            for (PostCar postCar : postCarsWaiting) {
                if (postCar != null) {
                    nb++;
                }
            }

            return nb;
        }
    }
}
