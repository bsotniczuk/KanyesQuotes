import controller.MainController;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String info = "Enter number of Kanyes quotes to fetch from api.kanye.rest\nnumber must be between 5 and 20: ";
        System.out.print(info);
        while (!sc.hasNextInt()) {
            System.out.print(info);
            sc.next();
        }
        int number = sc.nextInt();

        MainController mainController = new MainController(number);
        mainController.generateQuotes();
    }
}
