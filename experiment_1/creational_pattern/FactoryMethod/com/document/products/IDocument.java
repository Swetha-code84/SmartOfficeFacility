package com.document.products;
public interface IDocument {
    void open();
    void save();
    void close();
    void initialize(String content);
}
