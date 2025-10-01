package com.document.products;

public class TextDocument implements IDocument {
    private String name;
    private String content; // NEW

    public TextDocument(String name) {
        this.name = name;
    }

    @Override
    public void initialize(String content) {
        this.content = content;
        System.out.println("[INITIALIZE]: Text Document initialized with content: '" + content + "'");
    }

    @Override
    public void open() {
        System.out.println("[DOCUMENT]: Opening Text Document: " + name + " (Content: " + content + ")");
    }

    // ... (save and close methods remain the same) ...
    @Override
    public void save() {
        System.out.println("[DOCUMENT]: Saving Text Document: " + name);
    }

    @Override
    public void close() {
        System.out.println("[DOCUMENT]: Closing Text Document: " + name);
    }
}