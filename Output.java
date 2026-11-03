import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Output {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public static synchronized void print(String type, String message) {
        String time = LocalTime.now().format(FORMAT);

        System.out.printf("[%s] [%-10s] %s%n",
                time, type, message);
    }

    public static void header(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.printf("%35s%n", title);
        System.out.println("============================================================");
    }

    public static void section(String title) {
        System.out.println();
        System.out.println("[ " + title + " ]");
        System.out.println("------------------------------------------------------------");
    }

    public static void success(String message) {
        print("SUCCESS", message);
    }
}