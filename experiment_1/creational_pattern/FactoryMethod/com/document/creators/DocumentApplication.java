package com.document.creators;
import com.document.products.IDocument;
public abstract class DocumentApplication {
    private static int documentCounter = 0;
    public abstract String getApplicationType();
    public abstract String getDefaultContent();
    public abstract IDocument createDocument(String name);
    public void runApplication() {
        documentCounter++;
        String documentName = getApplicationType() + " Document " + documentCounter;
        System.out.println("\n[APPLICATION]: Starting " + getApplicationType() + " Application...");
        IDocument doc = createDocument(documentName);
        doc.initialize(getDefaultContent());
        doc.open();
        System.out.println("[APPLICATION]: Document object successfully created and initialized.");
    }
}
