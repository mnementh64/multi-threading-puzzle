package com.hack.multithread.office;

import com.hack.multithread.actors.Customer;
import com.hack.multithread.actors.PostCar;

public class PostOffice extends Thread {

    private final FrontOffice frontOffice;
    private final BackOffice backOffice;

    private boolean okToRun = true;

    public PostOffice(FrontOffice frontOffice, BackOffice backOffice) {
        this.frontOffice = frontOffice;
        this.backOffice = backOffice;
    }

    @Override
    public synchronized void start() {
        System.out.println(" ________  ________  ________  _________               ________  ________ ________ ___  ________  _______      \n" +
                "|\\   __  \\|\\   __  \\|\\   ____\\|\\___   ___\\            |\\   __  \\|\\  _____\\\\  _____\\\\  \\|\\   ____\\|\\  ___ \\     \n" +
                "\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\___|\\|___ \\  \\_|____________\\ \\  \\|\\  \\ \\  \\__/\\ \\  \\__/\\ \\  \\ \\  \\___|\\ \\   __/|    \n" +
                " \\ \\   ____\\ \\  \\\\\\  \\ \\_____  \\   \\ \\  \\|\\____________\\ \\  \\\\\\  \\ \\   __\\\\ \\   __\\\\ \\  \\ \\  \\    \\ \\  \\_|/__  \n" +
                "  \\ \\  \\___|\\ \\  \\\\\\  \\|____|\\  \\   \\ \\  \\|____________|\\ \\  \\\\\\  \\ \\  \\_| \\ \\  \\_| \\ \\  \\ \\  \\____\\ \\  \\_|\\ \\ \n" +
                "   \\ \\__\\    \\ \\_______\\____\\_\\  \\   \\ \\__\\              \\ \\_______\\ \\__\\   \\ \\__\\   \\ \\__\\ \\_______\\ \\_______\\\n" +
                "    \\|__|     \\|_______|\\_________\\   \\|__|               \\|_______|\\|__|    \\|__|    \\|__|\\|_______|\\|_______|\n" +
                "                       \\|_________|                                                                            \n" +
                "                                                                                                               \n" +
                "                                                                                                               \n" +
                " ________  ___  _____ ______   ___  ___  ___       ________  _________  ___  ________  ________                \n" +
                "|\\   ____\\|\\  \\|\\   _ \\  _   \\|\\  \\|\\  \\|\\  \\     |\\   __  \\|\\___   ___\\\\  \\|\\   __  \\|\\   ___  \\              \n" +
                "\\ \\  \\___|\\ \\  \\ \\  \\\\\\__\\ \\  \\ \\  \\\\\\  \\ \\  \\    \\ \\  \\|\\  \\|___ \\  \\_\\ \\  \\ \\  \\|\\  \\ \\  \\\\ \\  \\             \n" +
                " \\ \\_____  \\ \\  \\ \\  \\\\|__| \\  \\ \\  \\\\\\  \\ \\  \\    \\ \\   __  \\   \\ \\  \\ \\ \\  \\ \\  \\\\\\  \\ \\  \\\\ \\  \\            \n" +
                "  \\|____|\\  \\ \\  \\ \\  \\    \\ \\  \\ \\  \\\\\\  \\ \\  \\____\\ \\  \\ \\  \\   \\ \\  \\ \\ \\  \\ \\  \\\\\\  \\ \\  \\\\ \\  \\           \n" +
                "    ____\\_\\  \\ \\__\\ \\__\\    \\ \\__\\ \\_______\\ \\_______\\ \\__\\ \\__\\   \\ \\__\\ \\ \\__\\ \\_______\\ \\__\\\\ \\__\\          \n" +
                "   |\\_________\\|__|\\|__|     \\|__|\\|_______|\\|_______|\\|__|\\|__|    \\|__|  \\|__|\\|_______|\\|__| \\|__|          \n" +
                "   \\|_________|                                                                                                \n" +
                "                                                                                                               \n" +
                "                                                                                                               ");
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
