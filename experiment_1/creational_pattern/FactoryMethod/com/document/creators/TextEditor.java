package com.document.creators;
import com.document.products.IDocument;
import com.document.products.TextDocument;
public class TextEditor extends DocumentApplication {
    @Override
    public IDocument createDocument(String name) {
        System.out.println("[FACTORY METHOD]: Creating TextDocument object...");
        return new TextDocument(name);
    }
    @Override
    public String getApplicationType() {
        return "Text";
    }
    @Override
    public String getDefaultContent() {
        return "Start typing here...";
    }
}
