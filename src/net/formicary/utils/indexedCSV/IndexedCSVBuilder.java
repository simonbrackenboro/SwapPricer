package net.formicary.utils.indexedCSV;

public interface IndexedCSVBuilder {
    IndexedCSVBuilder withFile(String file);
    IndexedCSV build();
}
