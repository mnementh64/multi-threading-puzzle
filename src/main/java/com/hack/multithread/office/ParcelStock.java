package com.hack.multithread.office;

public class ParcelStock {
    private final Object stockMutex = new Object();
    private int nbParcels = 0;

    public ParcelStock(int initialNumberOfParcels) {
        this.nbParcels = initialNumberOfParcels;
    }

    public void increment() {
        synchronized (stockMutex) {
            nbParcels++;
            System.out.println(System.nanoTime() + " : " + nbParcels + " parcels in stock");
        }
    }

    public void decrement() {
        synchronized (stockMutex) {
            nbParcels--;
            System.out.println(System.nanoTime() + " : " + nbParcels + " parcels in stock");
        }
    }

    public int nbAvailableParcels() {
        synchronized (stockMutex) {
            return nbParcels;
        }
    }
}
