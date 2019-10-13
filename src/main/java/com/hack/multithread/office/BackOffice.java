package com.hack.multithread.office;

import com.hack.multithread.actors.Clerk;
import com.hack.multithread.actors.PostCar;

import java.util.NoSuchElementException;

public class BackOffice {
    private final Object carMutex = new Object();
    private PostCar[] postCarsWaiting;
    private final Clerk clerk;
    private final ParcelStock parcelStock;
    private final int nbCars;

    public BackOffice(Clerk clerk, ParcelStock parcelStock, int nbCars) {
        this.clerk = clerk;
        this.parcelStock = parcelStock;
        this.nbCars = nbCars;
        this.postCarsWaiting = new PostCar[nbCars]; // no car at the post office at the beginning
    }

    void tryToPark(PostCar newPostCar) {
        synchronized (carMutex) {
            try {
                // a place should available - just occupy it
                postCarsWaiting[firstFreeIndex(postCarsWaiting)] = newPostCar;
                newPostCar.setStatus(PostCar.WAITING_FOR_PARCEL);
                System.out.println(System.nanoTime() + " :     " + nbCarsAtTheOffice() + " cars waiting and " + parcelStock.nbAvailableParcels() + " parcels in stock");

                maybeAwakeClerkForCars();
            } catch (NoSuchElementException e) {
                // no free place, try next time.
            } finally {
                carMutex.notifyAll();
            }
        }
    }

    void maybeAwakeClerkForCars() {
        synchronized (carMutex) {
            try {
                // all post cars at the office and enough parcels ? Awake the clerk !
                if (nbCarsAtTheOffice() == nbCars && parcelStock.nbAvailableParcels() >= nbCars) {
                    clerk.askedToPutParcelsIntoPostCars(postCarsWaiting);
                }
            } finally {
                carMutex.notifyAll();
            }
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

    private int firstFreeIndex(Object[] arrayOfValues) {
        for (int i = 0; i < arrayOfValues.length; i++) {
            if (arrayOfValues[i] == null) {
                return i;
            }
        }
        throw new NoSuchElementException("No free index in this array of values (size " + arrayOfValues.length + ")");
    }
}
