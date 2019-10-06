package com.hack.multithread;

import com.hack.multithread.actors.Customer;
import com.hack.multithread.actors.PostCar;
import com.hack.multithread.actors.PostOffice;

import java.time.Duration;

public class PostOfficeSimulation {

    private static final int NB_CUSTOMERS = 10;
    private static final int NB_CARS = 9;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Post-office simulation ...................");

        PostOffice postOffice = new PostOffice(3, 9);
        postOffice.start();

        // create all customers and make them alive
        for (int i = 0; i < NB_CUSTOMERS; i++) {
            Customer customer = new Customer(String.valueOf(i + 1), postOffice);
            customer.start();
        }

        // create all post cars and make them alive - they start delivering
        for (int i = 0; i < NB_CARS; i++) {
            PostCar postCar = new PostCar(String.valueOf(i + 1), postOffice);
            postCar.start();
        }

        Thread.sleep(Duration.ofMinutes(10).toMillis());
    }
}
