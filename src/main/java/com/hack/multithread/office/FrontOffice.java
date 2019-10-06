package com.hack.multithread.office;

import com.hack.multithread.actors.Clerk;
import com.hack.multithread.actors.Customer;

import java.util.NoSuchElementException;

class FrontOffice {
    private final Object seatMutex = new Object();
    private Customer[] customersSit;
    private final Clerk clerk;
    private final int nbSeats;

    FrontOffice(Clerk clerk, int nbSeats) {
        this.clerk = clerk;
        this.nbSeats = nbSeats;
        this.customersSit = new Customer[nbSeats]; // no customer at the beginning
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

    void maybeAwakeClerkForCustomers() {
        synchronized (seatMutex) {
            try {
                // all seats occupied ? Awake the clerk !
                if (nbSeatsOccupied() == nbSeats) {
                    clerk.receiveCustomers(customersSit);
                }
            } finally {
                seatMutex.notifyAll();
            }
        }
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

    private int firstFreeIndex(Object[] arrayOfValues) {
        for (int i = 0; i < arrayOfValues.length; i++) {
            if (arrayOfValues[i] == null) {
                return i;
            }
        }
        throw new NoSuchElementException("No free index in this array of values (size " + arrayOfValues.length + ")");
    }
}
