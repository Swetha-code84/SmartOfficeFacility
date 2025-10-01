package com.document.products;

// Concrete Product: Spreadsheet Document
public class SpreadsheetDocument implements IDocument {
    private String name;
    private String content; // New field to store initialization content

    public SpreadsheetDocument(String name) {
        this.name = name;
    }

    // --- NEW: Implementation of the abstract method from IDocument ---
    @Override
    public void initialize(String content) {
        this.content = content;
        System.out.println("[INITIALIZE]: Spreadsheet Document initialized with content: '" + content + "'");
    }

    @Override
    public void open() {
        // Display the document name and its initialized content
        System.out.println("[DOCUMENT]: Opening Spreadsheet Document: " + name + " (Content: " + content + ")");
    }

    @Override
    public void save() {
        System.out.println("[DOCUMENT]: Saving Spreadsheet Document: " + name);
    }

    @Override
    public void close() {
        System.out.println("[DOCUMENT]: Closing Spreadsheet Document: " + name);
    }
}