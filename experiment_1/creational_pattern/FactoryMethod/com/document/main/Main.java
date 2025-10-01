package com.document.main;
import com.document.creators.DocumentApplication;
import com.document.creators.SpreadsheetApp;
import com.document.creators.TextEditor;
import com.document.products.IDocument;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DocumentApplication app = null;
        String choice;
        System.out.println("--- Factory Method Pattern: Interactive Document Creator ---");
        System.out.println("Select the Application to Run:");
        System.out.println("1: Text Editor (Creates Text Documents)");
        System.out.println("2: Spreadsheet App (Creates Spreadsheet Documents)");
        System.out.print("Enter choice (1 or 2): ");
        choice = scanner.nextLine();
        switch (choice) {
            case "1":
                app = new TextEditor();
                break;
            case "2":
                app = new SpreadsheetApp();
                break;
            default:
                System.err.println("\nInvalid choice. Defaulting to Text Editor.");
                app = new TextEditor();
        }

        if (app != null) {
            app.runApplication();
            System.out.print("\n[USER ACTION]: Write your content (one line only, press Enter to proceed): ");
            String userInput = scanner.nextLine(); 
            System.out.println("\n[USER ACTION]: Document content simulated and saved.");
            System.out.print("\n[USER ACTION]: Display saved content now (y/n)? ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("\n--- SAVED CONTENT ---");
                System.out.println(userInput);
                System.out.println("---------------------");
            }
        }

        System.out.println("[APPLICATION]: Application shutdown complete.");
        scanner.close();
        System.out.println("\nProcess finished with exit code 0");
    }
}
