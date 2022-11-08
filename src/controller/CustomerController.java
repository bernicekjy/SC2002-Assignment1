package controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import model.Booking;
import model.Cinema;
import model.Cineplex;
import model.MovieListing;
import model.SerializationUtil;
import model.Showtime;
import model.Ticket;
import model.Ticket.TicketType;
import model.Transaction;
import model.Vendor;
import model.Cinema.CinemaClass;
import model.Movie.ShowingStatus;

public class CustomerController {
    /**
     * Displays the top 5 movie listings by ticket sales or reviewer ratings
     * @param bySales
     */
    public static void displayTopMovieListings() {
    	ArrayList<Object> mListings = new ArrayList<>();
    	ArrayList<MovieListing> castedListings = new ArrayList<>();
    	MovieListing mListing = null;
		String filterVal = "";
		boolean invalid = true;
		boolean bySales = true;

		// read set filter value from file
		Path path = Paths.get("filter.txt");
		try {
			filterVal = Files.readString(path, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("=============== TOP MOVIES =============== ");

		// if filter value is set by admin, user cannot choose, otherwise they can choose
		if(filterVal == "ratings") {
			bySales = false;
		}
		else if(filterVal == "any") {
			while(invalid) {
				System.out.println("Do you want to filter by ticket sales (1) or overall rating (2)? ");
				switch(InputController.getIntRange(1, 2)) {
					case 1:
						bySales = true;
						invalid = false;
						break;
					case 2:
						bySales = false;
						invalid = false;
						break;
					default:
						System.out.println("Invalid option");
						break;
					}
			}
		}
    	
    	try {
			mListings = SerializationUtil.deserialize("movieListings.ser");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	for(int i=0;i<mListings.size();i++) {
    		mListing = (MovieListing)mListings.get(i);
    		if(!bySales) {
    			mListing.setBySales(false);
    		}
    		castedListings.add(mListing);
    	}
    	
    	// sort the castedListings ArrayList to get the sorted movie listings
    	Collections.sort(castedListings);
    	
    	// display the top 5 movie listings by sales/ratings
    	for(int i=0;i<5;i++) {
    		System.out.print((i+1) + ". ");
    		mListing = castedListings.get(i);
    		mListing.printInfo(false);
    	}
    }
    
    /**
     * Called to print all movie listing information.
     */
    public static void displayAllMovieListings() {
    	ArrayList<Object> movieListings = MovieListingController.readMovieListingsFile();
    	MovieListing movieListing;
    	
		System.out.println("=============== ALL MOVIES =============== ");
    	for(int i = 0; i < movieListings.size(); i++) {
    		movieListing = (MovieListing) movieListings.get(i);
    		movieListing.printInfo(false);
    	}
    }

	public static void displayShowingMovieListings() {
		System.out.println("Current Movies Available for Booking: ");

		int numberMoviesShowing = 1;
		ArrayList<Object> movieListings = MovieListingController.readMovieListingsFile();


		for (int i = 0; i < movieListings.size(); i++) {
			MovieListing currentMovieListing = (MovieListing) movieListings.get(i);
			if (currentMovieListing.getMovie().getStatus() == ShowingStatus.NOW_SHOWING || currentMovieListing.getMovie().getStatus() == ShowingStatus.PREVIEW) {
				System.out.print(numberMoviesShowing + ": ");
				currentMovieListing.printSimpleInfo();
				numberMoviesShowing++;
			}
		}
	}

	/**
     * Called to print details of a specific movie listing
     */
	public static void displaySpecificListing() {
		ArrayList<Object> mListings = new ArrayList<>();
    	MovieListing mListing = null;
		int selection = 0;

		try {
			mListings = SerializationUtil.deserialize("movieListings.ser");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	for(int i=0;i<mListings.size();i++) {
    		mListing = (MovieListing)mListings.get(i);
    		System.out.println((i+1) + ". " + mListing.getMovie().getTitle());
    	}

		System.out.println("Which movie do you want to view details of? ");
		selection = InputController.getIntRange(1, mListings.size());

		mListing = (MovieListing)mListings.get(selection-1);

		mListing.printInfo(true);

	}

	/**
     * Called to print seating chart for a particular showtime
	 * @return int
     */
	public static int checkAvailableSeats() {
		ArrayList<Object> cineplexesInfo = new ArrayList<>();
		ArrayList<Cineplex> cineplexes = new ArrayList<>();
    	ArrayList<Cinema> cinemas = new ArrayList<>();
		ArrayList<Object> mListings = new ArrayList<>();
		ArrayList<Showtime> showtimes = new ArrayList<>();
		ArrayList<Showtime> matchingShowtimes = new ArrayList<>();
		Vendor vendor = null;
		MovieListing mListing = null;
		Showtime showtime = null;
		Cineplex cineplex = null;
		CinemaClass cinemaClass;
		int count;
		int selection = 0;
		String usrInput, location;
		LocalDate filterDate;

		// dates will be formatted into YYYY-MM-DD format
    	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		Scanner sc = new Scanner(System.in);

		System.out.println("=============== SEAT AVAILABILITY =============== ");
		System.out.println("Available cineplexes: ");
    	try {
			cineplexesInfo = SerializationUtil.deserialize("cineplexes.ser");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Unable to read cineplexes info.");
			return 0;
		}
    	
    	
    	for(int i=0;i<cineplexesInfo.size();i++) {
    		System.out.println((i+1) + ". " + ((Cineplex)cineplexesInfo.get(i)).getLocation());
    	}
    	
		System.out.println("Enter the cineplex to view the showtimes: ");
		selection = InputController.getIntRange(1, cineplexesInfo.size());
    	
		cineplex = (Cineplex)cineplexesInfo.get(selection-1);
		location = cineplex.getLocation();

		System.out.println("Cinema classes: ");
		count = 1;
    	for(CinemaClass status : CinemaClass.values()) {
    		System.out.println(count + ". " + status);
    		count++;
    	}
		
		System.out.println("Enter the desired cinema class");
		selection = InputController.getIntRange(1, CinemaClass.values().length);

		cinemaClass = CinemaClass.values()[selection-1];

    	System.out.println("Available movies to check showtimes for: ");
    	try {
			mListings = SerializationUtil.deserialize("movieListings.ser");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Unable to read movie listings.");
			return 0;
		}
    	
    	for(int i=0;i<mListings.size();i++) {
    		mListing = (MovieListing)mListings.get(i);
    		System.out.println((i+1) + ". " + mListing.getMovie().getTitle());
    	}
    	
		System.out.println("Which movie do you want to view the showtimes for: ");
		selection = InputController.getIntRange(1, mListings.size());
    	
    	mListing = (MovieListing)mListings.get(selection-1);

		showtimes = mListing.getShowtimes();

    	System.out.println("Enter the date to view showtimes for YYYY/MM/DD (E.g. 2022/10/03): ");
    	filterDate = InputController.getDate();

		// filter out showtimes available for the chosen cineplex and cinemaClass on selected date only
		for(int i=0;i<showtimes.size();i++) {
			if(showtimes.get(i).getLocation() == location && showtimes.get(i).getCinemaBooking().getCinemaClass() == cinemaClass && showtimes.get(i).getDate().isEqual(filterDate)) {
				matchingShowtimes.add(showtimes.get(i));
			}
		}
		
		if(matchingShowtimes.size() == 0) {
			System.out.println("No showtime for the date entered!");
			return 0;
		}

		for(int i=0;i<matchingShowtimes.size();i++) {
			System.out.println((i+1) + ". Date: " + matchingShowtimes.get(i).getDate() + " Time: " + matchingShowtimes.get(i).getStart());
		}

		System.out.println("Which showtime do you want to check seat availability of: ");
		selection = InputController.getIntRange(1, matchingShowtimes.size());

		showtime = matchingShowtimes.get(selection-1);

		// print the seats
		showtime.getCinemaBooking().printSeats();

		return 1;
	}

	/*
	 * 1. Display all movie listings
	 * 2. Display and Choose Cineplex
	 * 3. Choose cinema class 
	 * 4. Choose Movie 
	 * 5. List and Choose Showtime
	 * 6. List and Choose Ticket Type
	 * 7. List and Choose Seat 
	 * 8. Generate ticket
	 * 9. Compute Price
	 * 10. Perform Transaction
	 * 		10a. Prompt for email and mobile number
	 * 11. Save booking
	 */
	public static void makeBooking() {
		// Step 1 - List all movie listings that are available for booking
		displayAllMovieListings();
		System.out.println();

		// Step 2 - Display all cineplexes for Cathay, and let user choose one
		// @return chosenCineplex
		// !!! Future feature: let user choose vendor
		ArrayList<Object> vendors = VendorController.readVendorsFile();
		Vendor cathay = (Vendor) vendors.get(0); // only have 1 vendor
		Booking booking = new Booking("B001", LocalDate.now());

		boolean cineplexDone = false;
		Cineplex chosenCineplex = null;
		ArrayList<Object> movieListings = MovieListingController.readMovieListingsFile();
		while (!cineplexDone) {
			chosenCineplex = CineplexController.chooseCineplex(cathay);
			if (chosenCineplex == null) { // exit booking
				return;
			}

			// check if cineplex has movieListings, else pick another cineplex
			int numberShowtimes = 0;
			for (int i = 0; i < movieListings.size(); i++) {
				MovieListing currentMovieListing = (MovieListing) movieListings.get(i);
				for (int j = 0; j < currentMovieListing.getShowtimes().size(); j++) {
					Showtime currentShowtime = currentMovieListing.getShowtimes().get(j);
					if (chosenCineplex.getLocation().equals(currentShowtime.getLocation())) {
						numberShowtimes++;
					}
				}
			}
			if (numberShowtimes == 0) {
				System.out.println("Chosen cineplex has no movies showing! Please pick another cineplex.");
			}
			else {
				cineplexDone = true;
			}
		}
		System.out.println();

		// Step 3 - Choose cinema class
		System.out.println("Please choose an available cinema class:");
		ArrayList<CinemaClass> presentCinemaClasses = chosenCineplex.getCineplexCinemaClasses();
		int counter = 1;
		for (CinemaClass status: presentCinemaClasses) {
			System.out.println(counter + ": " + status);
			counter++;
		}

		CinemaClass chosenCinemaClass;
		int chosenCinemaClassChoice = InputController.getIntRange(1, presentCinemaClasses.size());
		chosenCinemaClass = presentCinemaClasses.get(chosenCinemaClassChoice - 1);
		System.out.println();
		
		// Step 4 - Choose Movie
		System.out.print("Movies Showing: ");
		int movieChoice = InputController.getInt();
		MovieListing chosenMovieListing = (MovieListing) movieListings.get(movieChoice - 1);
		if (chosenMovieListing.getMovie().getStatus() == ShowingStatus.COMING_SOON || chosenMovieListing.getMovie().getStatus() == ShowingStatus.END_OF_SHOWING) {
			System.out.println("This movie is not available for booking");
			return;
		}
		System.out.println();

		// Step 5 - List and Choose Showtime
		// If chosen movie has showtimes in chosenCineplex, then list them 
		ArrayList<Showtime> showtimeChoices = new ArrayList<>();
		System.out.println("Showtimes: ");
		for (int i = 0; i < chosenMovieListing.getShowtimes().size(); i++) {
			Showtime currentShowtime = chosenMovieListing.getShowtimes().get(i);
			if (chosenCineplex.getLocation().equals(currentShowtime.getLocation()) && (currentShowtime.getCinemaBooking().getCinemaClass().equals(chosenCinemaClass))) {
				showtimeChoices.add(currentShowtime);
				System.out.print((i + 1) + ": ");
				currentShowtime.printShowtime();
			}
		}

		System.out.print("Choose the showtime: ");
		int showtimeChoice = InputController.getInt();
		Showtime chosenShowtime = showtimeChoices.get(showtimeChoice - 1);
		System.out.println();
		
		// Step 6 - List and Choose Ticket Types
		for (int i = 0; i < TicketType.values().length; i++) {
			System.out.println((i + 1) + ": " + TicketType.values()[i]);
		}
		System.out.print("Select Ticket Type: ");
		int chosenType = InputController.getIntRange(1, TicketType.values().length) - 1;
		System.out.println();

		// Step 7 - List and Choose Seat(s)
		// Step 8 - Generate Ticket for each seat chosen
		boolean seatChosen = false;
		boolean anotherSeatChoice = true;
		int seatsChosen = 0;
		int row, col;
		ArrayList<Ticket> holdingTickets;
		while (!seatChosen && anotherSeatChoice) {
			chosenShowtime.getCinemaBooking().printSeats();
			System.out.print("Please enter the row number: ");
			row = InputController.getIntRange(1, chosenShowtime.getCinemaBooking().getNumRows());
			System.out.print("Please enter the col number: ");
			col = InputController.getIntRange(1, chosenShowtime.getCinemaBooking().getNumCols());
			
			if (!chosenShowtime.getCinemaBooking().getSeats()[row - 1][col - 1].getAssigned()) {
				seatChosen = true;
				seatsChosen++;
				chosenShowtime.getCinemaBooking().getSeats()[row - 1][col - 1].assignSeat();
				Ticket t = new Ticket("T001", chosenShowtime.getShowtimeId(), chosenShowtime.getCinemaCode(), chosenMovieListing.getMovie().getTitle(), TicketType.values()[chosenType], "R"+row+"C"+col);
				PriceController.computePrice(t, chosenShowtime, chosenCinemaClass, chosenMovieListing.getMovie());
				booking.getTickets().add(t);
				System.out.println("Would you like to choose another seat? (y/n)");
				anotherSeatChoice = InputController.getBoolean();
				if (anotherSeatChoice) {
					seatChosen = false;
				}
				else {
					anotherSeatChoice = false;
				}
			}
			else {
				System.out.println("Seat has been taken");
			}
		}
		System.out.println();
		
		for (int i = 0; i < booking.getTickets().size(); i++) {
			booking.getTickets().get(i).printTicket();
			System.out.println();
		}
		System.out.println("Total Cost = " + booking.getTotalCost());
		System.out.println("Continue with payment? (y/n");
		boolean continueTransaction = InputController.getBoolean();
		if (!continueTransaction) {
			return;
		}
		// prompt for email and mobile phone
		System.out.print("Enter email address: ");
		String email = InputController.getEmail();
		System.out.print("Enter mobile number: ");
		String mobileNo = InputController.getMobileNumber();
		Transaction transaction = new Transaction("T001", booking, email, mobileNo);
		transaction.printTransaction();
		
		//Serialize transaction
		TransactionController.saveTicketsFile(transaction);
		System.out.println("Transaction Successful!");
	}
}
