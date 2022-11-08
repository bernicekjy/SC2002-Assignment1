package model;

import java.util.ArrayList;
import java.util.Objects;

import model.Cinema.CinemaClass;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Cineplex implements Serializable {
    @Serial
    private static final long serialVersionUID = 123458L;
    private String location;
    private ArrayList<Cinema> cinemas;
    private int numCinemas;

    public Cineplex(String location) {
        this.location = location;
        this.cinemas = new ArrayList<>();
        this.numCinemas=0;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNumCinemas() {
        return this.numCinemas;
    }

    public ArrayList<Cinema> getCinemas() {
        return cinemas;
    }

    public Cinema getCinema(int i) {
        return this.cinemas.get(i);
    }

    public void addNewCinema(Cinema newCinema){
        this.cinemas.add(newCinema);
        this.numCinemas++;
    }

    public ArrayList<CinemaClass> getCineplexCinemaClasses() {
        ArrayList<CinemaClass> presentCinemaClasses = new ArrayList<>();
        for (int i = 0; i < getCinemas().size(); i++) {
            Cinema currentCinema = getCinema(i);
            if (!presentCinemaClasses.contains(currentCinema.getCinemaClass())) {
                presentCinemaClasses.add(currentCinema.getCinemaClass());
            }
        }
        return presentCinemaClasses;
    }

    public void printCinemas(){
        System.out.println("Cinemas at "+ this.location +": \n");

        //print cinemas
        for (int i = 0; i < numCinemas; i++) {
            Cinema currentCinema = this.cinemas.get(i);

            System.out.println("============== " + (i + 1) + " =============");
            System.out.println(currentCinema.cinemaTypestoString(currentCinema.getCinemaClass()));
            System.out.println("Cinema Code: " + currentCinema.getCinemaCode());
            System.out.println("Number of seats: " + currentCinema.getNumSeats());
            System.out.println();
            System.out.println("Cinemas at "+this.location+": \n");
        }

        // int[] cinemasPrinted = new int[2];
        // //initialising values in array
        // for (int i=0;i<CinemaTypes.values().length;i++){
        //     cinemasPrinted[i]=0;//0 for not printed, 1 for printed
        // }

        // //print cinemas
        // for (int i=0;i<numCinemas;i++){
        //     if (cinemasPrinted[this.cinemas.get(i).getCinemaName().ordinal()]==0) {
        //         System.out.println(cinemaTypestoString(this.cinemas.get(i).getCinemaName()));
        //         System.out.println("Number of seats: " + this.cinemas.get(i).getNumSeats());
        //         System.out.println("Description: " + this.cinemas.get(i).getCinemaDetails());
        //         System.out.println();

        //         cinemasPrinted[this.cinemas.get(i).getCinemaName().ordinal()]=1;
        //     }
        // }
    }

    

}
