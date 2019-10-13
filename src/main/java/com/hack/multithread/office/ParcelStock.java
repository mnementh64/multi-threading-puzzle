package com.hack.multithread.office;

public class ParcelStock {
    private final Object stockMutex = new Object();
    private int nbParcels = 0;

    public ParcelStock(int initialNumberOfParcels) {
        this.nbParcels = initialNumberOfParcels;
    }

    public void add(int nbParcels) {
        synchronized (stockMutex) {
            this.nbParcels += nbParcels;
            System.out.println(System.nanoTime() + " : ------------ add " + nbParcels + " parcels - now have " + this.nbParcels + " parcels in stock");
        }
    }

    public void decrement() {
        synchronized (stockMutex) {
            nbParcels--;
            System.out.println(System.nanoTime() + " : ------------ remove 1 parcel - now have " + nbParcels + " parcels in stock");
        }
    }

    int nbAvailableParcels() {
        synchronized (stockMutex) {
            return nbParcels;
        }
    }
}
