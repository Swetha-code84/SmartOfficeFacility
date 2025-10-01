package com.document.creators;

import com.document.products.IDocument;
// We keep the Scanner import only for the main client class now.
// The DocumentApplication should not rely on Scanner directly.

public abstract class DocumentApplication {
    private static int documentCounter = 0;

    // Abstract Factory Methods (Define context and creation)
    public abstract String getApplicationType();
    public abstract String getDefaultContent();
    public abstract IDocument createDocument(String name);

    // THIS is the core Template Method
    public void runApplication() {
        documentCounter++;
        String documentName = getApplicationType() + " Document " + documentCounter;

        System.out.println("\n[APPLICATION]: Starting " + getApplicationType() + " Application...");

        // 1. FACTORY METHOD CALL: Creates the document
        IDocument doc = createDocument(documentName);

        // 2. ADVANCED INITIALIZATION: Uses the context from the Concrete Creator
        doc.initialize(getDefaultContent());

        doc.open();
        System.out.println("[APPLICATION]: Document object successfully created and initialized.");
        // The rest of the interactive flow will be managed by the Main class (Client).
    }
}