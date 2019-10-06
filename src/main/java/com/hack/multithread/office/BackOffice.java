package com.hack.multithread.office;

import com.hack.multithread.actors.Clerk;
import com.hack.multithread.actors.PostCar;

import java.util.NoSuchElementException;

class BackOffice {
    private final Object carMutex = new Object();
    private PostCar[] postCarsWaiting;
    private final Clerk clerk;
    private final int nbCars;

    BackOffice(Clerk clerk, int nbCars) {
        this.clerk = clerk;
        this.nbCars = nbCars;
        this.postCarsWaiting = new PostCar[nbCars]; // no car at the post office at the beginning
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

    void maybeAwakeClerkForCars() {
        synchronized (carMutex) {
            try {
                // all post cars at the office ? Awake the clerk !
                if (nbCarsAtTheOffice() == nbCars) {
                    clerk.putParcelsIntoPostCars(postCarsWaiting);
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
