package BHive;

import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class HiveReader {
    private static EventReader eventReader;
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        eventReader = new EventReader(factory, "rafael");
        eventReader.startListening("birth");
        eventReader.startListening("death");
        eventReader.startListening("mating");
        eventReader.startListening("food");

        startInputThread(eventReader);
    }
    private static void startInputThread(EventReader eventReader) {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Press 'p' to print counters, r to reset them or 'q' to quit:");
                String input = scanner.nextLine();
                if ("p".equals(input)) {
                    eventReader.printCounters();
                } else if ("r".equals(input)){
                    eventReader.resetCounters();
                }
                else if ("q".equals(input)) {
                    break; // Exit the loop, but don't interrupt the thread
                }
            }
            scanner.close();
        });
        inputThread.start();
    }
}
