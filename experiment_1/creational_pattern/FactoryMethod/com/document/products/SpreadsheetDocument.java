package com.document.products;
public class SpreadsheetDocument implements IDocument {
    private String name;
    private String content;
    public SpreadsheetDocument(String name) {
        this.name = name;
    }
    @Override
    public void initialize(String content) {
        this.content = content;
        System.out.println("[INITIALIZE]: Spreadsheet Document initialized with content: '" + content + "'");
    }
    @Override
    public void open() {
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
