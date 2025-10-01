package com.document.creators;

import com.document.products.IDocument;
import com.document.products.SpreadsheetDocument;

public class SpreadsheetApp extends DocumentApplication {

    @Override
    public IDocument createDocument(String name) {
        System.out.println("[FACTORY METHOD]: Creating SpreadsheetDocument object...");
        return new SpreadsheetDocument(name);
    }

    @Override
    public String getApplicationType() {
        return "Spreadsheet";
    }

    // NEW: Provides the unique starting content for Spreadsheet Documents
    @Override
    public String getDefaultContent() {
        return "Default 3 columns, 10 rows, ready for data entry.";
    }
}