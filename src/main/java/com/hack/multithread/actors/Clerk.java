package com.hack.multithread.actors;

public class Clerk {

    private final Object clerkMutex = new Object();

    public void receiveCustomers(Customer[] customersSit) {
        synchronized (clerkMutex) {
            try {
                System.out.println(System.nanoTime() + " : Clerk is awaken ! Back to work for customers.");
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
                for (int i = 0; i < customersSit.length; i++) {
                    customersSit[i] = null;
                }
                System.out.println(System.nanoTime() + " : Clerk is sleeping again after a hard work with customers.");
            } finally {
                clerkMutex.notify();
            }
        }
    }

    public void putParcelsIntoPostCars(PostCar[] postCarsWaiting) {
        synchronized (clerkMutex) {
            try {
                System.out.println(System.nanoTime() + " : Clerk is awaken ! Back to work for post cars.");
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
                for (int i = 0; i < postCarsWaiting.length; i++) {
                    postCarsWaiting[i] = null;
                }
                System.out.println(System.nanoTime() + " : Clerk is sleeping again after a hard work with post cars.");
            } finally {
                clerkMutex.notifyAll();
            }
        }
    }
}