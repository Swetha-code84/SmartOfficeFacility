package com.document.products;

public interface IDocument {
    void open();
    void save();
    void close();

    // NEW: Method to initialize the document with content
    void initialize(String content);
}