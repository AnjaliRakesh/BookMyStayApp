import java.util.Scanner;

public class BookMyStayApp {

    public static void main(String[] args) {

        // UC1 — Welcome Message
        System.out.println("=================================");
        System.out.println("       BOOK MY STAY APP          ");
        System.out.println("   Hotel Booking Management      ");
        System.out.println("           Version 1.0           ");
        System.out.println("=================================");

        System.out.println("Application Started Successfully!");

        // UC2 — Menu + Input
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n1. Search Hotels");
        System.out.println("2. Book Room");
        System.out.println("3. Cancel Booking");
        System.out.println("4. Exit");

        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Searching hotels...");
                break;
            case 2:
                System.out.println("Room booking selected.");
                break;
            case 3:
                System.out.println("Booking cancellation selected.");
                break;
            case 4:
                System.out.println("Exiting application...");
                break;
            default:
                System.out.println("Invalid choice.");
        }

        scanner.close();
    }
}