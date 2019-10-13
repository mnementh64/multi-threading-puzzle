package com.hack.multithread;

import com.hack.multithread.actors.Clerk;
import com.hack.multithread.actors.Customer;
import com.hack.multithread.actors.PostCar;
import com.hack.multithread.office.BackOffice;
import com.hack.multithread.office.FrontOffice;
import com.hack.multithread.office.ParcelStock;
import com.hack.multithread.office.PostOffice;

public class PostOfficeSimulation {

    private static final int NB_CUSTOMERS = 10;
    private static final int NB_CARS = 9;
    private static final int NB_SEATS = 3;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Post-office simulation ...................");

        ParcelStock parcelStock = new ParcelStock(0);
        Clerk clerk = new Clerk(parcelStock);
        FrontOffice frontOffice = new FrontOffice(clerk, NB_SEATS);
        BackOffice backOffice = new BackOffice(clerk, parcelStock, NB_CARS);
        PostOffice postOffice = new PostOffice(frontOffice, backOffice);
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

        Thread.sleep(600000); // 10mn
    }
}
