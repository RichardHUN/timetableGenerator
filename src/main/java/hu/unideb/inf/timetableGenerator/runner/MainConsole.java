package hu.unideb.inf.timetableGenerator.runner;

import hu.unideb.inf.timetableGenerator.model.PlannedCourse;
import hu.unideb.inf.timetableGenerator.model.Room;
import hu.unideb.inf.timetableGenerator.model.Week;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MainConsole {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Week week = new Week();

        List<Room> rooms = readRooms();

        List<PlannedCourse> courses = readCourses();

        System.out.println(rooms);
        System.out.println(courses);

        System.out.println(week);

        System.out.println(Arrays.toString(args));
    }

    private static String readAndPrint(String message) {
        String input;
        System.out.print(message);
        input = scanner.nextLine();
        return input;
    }

    private static List<Room> readRooms() {
        List<Room> rooms = new ArrayList<>();
        String line;
        System.out.println("Enter room arguments. Type - as a room number to end room input:");
        while(true) {
            String roomNumber = readRoomNumber();

            if(roomNumber.equals("-")) {
                break;
            }

            String roomCapacity = readRoomCapacity();
            if(!isCorrectNumber(roomCapacity)) {
                System.out.println("Room capacity must be an integer. Please try again.");
                continue;
            }

            rooms.add(new Room(roomNumber, Integer.parseInt(roomCapacity)));
        }
        return rooms;
    }

    private static boolean isCorrectNumber(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static List<PlannedCourse> readCourses() {
        String courseName;
        String presenterName;
        int numberOfListeners;
        List<PlannedCourse> courses = new ArrayList<>();

        System.out.println("Enter course arguments. Type - as a course name to end course input:");

        while (true) {
            courseName = readCourseName();
            if (courseName.equals("-")) {
                break;
            }
            presenterName = readPresenterName();
            numberOfListeners = readNumberOfListeners();

            courses.add(new PlannedCourse(courseName, presenterName, numberOfListeners));
        }

        return courses;
    }

    private static String readRoomNumber() {
        return readAndPrint("Room number: ");
    }

    private static String readRoomCapacity() {
        return readAndPrint("Room capacity: ");
    }

    private static String readCourseName() {
        return readAndPrint("Enter course name: ");
    }

    private static String readPresenterName()  {
        return readAndPrint("Enter presenter name: ");
    }

    private static int readNumberOfListeners() {
        return Integer.parseInt(readAndPrint("Enter number of listeners: "));
    }

}
