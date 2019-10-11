package com.hack.multithread.actors;

public class Clerk {

    private static long TIME_TO_RECEIVE_ONE_CUSTOMER = 2000l;
    private static long TIME_TO_PUT_PARCEL_INTO_POST_CAR = 500l;

    private static int ACTION_SLEEP = 1;
    private static int ACTION_RECEIVE_CUSTOMERS = 2;
    private static int ACTION_PUT_PARCELS_IN_POST_CAR = 3;

    private final Object clerkMutex = new Object();
    private int currentActionType;
    private Thread currentAction;

    public Clerk() {
        sleep();
    }

    private void sleep() {
        synchronized (clerkMutex) {
            currentActionType = ACTION_SLEEP;
            currentAction = new Thread(makeClerkSleep()); // sleep at first
            currentAction.start();
        }
    }

    public void askedToReceiveCustomers(Customer[] customersSit) {
        synchronized (clerkMutex) {
            if (currentActionType == ACTION_SLEEP) {
                currentActionType = ACTION_RECEIVE_CUSTOMERS;
                currentAction.interrupt();
                currentAction = new Thread(doReceiveCustomers(customersSit));
                currentAction.start();
            }
        }
    }

    public void askedToPutParcelsIntoPostCars(PostCar[] postCarsWaiting) {
        synchronized (clerkMutex) {
            if (currentActionType != ACTION_PUT_PARCELS_IN_POST_CAR) {
                currentActionType = ACTION_PUT_PARCELS_IN_POST_CAR;
                currentAction.interrupt();
                currentAction = new Thread(doPutParcelsIntoPostCars(postCarsWaiting));
                currentAction.start();
            }
        }
    }

    private Runnable makeClerkSleep() {
        return () -> {
            try {
                Thread.sleep(100000L);
            } catch (InterruptedException e) {
                System.out.println(System.nanoTime() + " ******************************** CLERK INTERRUPTED - was sleeping");
                Thread.currentThread().interrupt();
                return;
            }
        };
    }

    private Runnable doReceiveCustomers(Customer[] customersSit) {
        return () -> {
            System.out.println(System.nanoTime() + " : Clerk is awaken ! Back to work for customers.");
            for (int i = 0; i < customersSit.length; i++) {
                customersSit[i].setStatus(Customer.POSTING);
                customersSit[i].prepareNextPosting();
                customersSit[i] = null;
                try {
                    Thread.sleep(TIME_TO_RECEIVE_ONE_CUSTOMER);
                } catch (InterruptedException e) {
                    System.out.println(System.nanoTime() + " ******************************** CLERK INTERRUPTED - was receiving customers");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println(System.nanoTime() + " : Clerk is sleeping again after a hard work with customers.");

            sleep();
        };
    }

    private Runnable doPutParcelsIntoPostCars(PostCar[] postCarsWaiting) {
        return () -> {
            System.out.println(System.nanoTime() + " : Clerk is awaken ! Back to work for post cars.");
            for (PostCar postCar : postCarsWaiting) {
                postCar.setStatus(PostCar.LOADED);
                try {
                    Thread.sleep(TIME_TO_PUT_PARCEL_INTO_POST_CAR);
                } catch (InterruptedException e) {
                    System.out.println(System.nanoTime() + " ******************************** CLERK INTERRUPTED - was putting parcels into cars");
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            // erase all post cars waiting --> parking places are free again
            for (int i = 0; i < postCarsWaiting.length; i++) {
                postCarsWaiting[i] = null;
            }
            System.out.println(System.nanoTime() + " : Clerk is sleeping again after a hard work with post cars.");

            sleep();
        };
    }
}
