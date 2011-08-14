package net.formicary.utils.indexedCSV;

import net.formicary.utils.indexedCSV.impl.TabEOFWindowsEOLCSVBuilder;
import net.formicary.utils.indexedCSV.impl.TabWindowsEOLCSVReader;

import java.util.List;
import org.slf4j.Logger;


public class CSVReader {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CSVReader.class);
    public static List<List<String>> get(String file) {
        log.info("Starting to index CSV {}", file);
        IndexedCSV indexedCSV = new TabEOFWindowsEOLCSVBuilder().withFile(file).build();
        log.info("Finished indexing CSV {}", file);
        return new TabWindowsEOLCSVReader(indexedCSV);
    }
}
