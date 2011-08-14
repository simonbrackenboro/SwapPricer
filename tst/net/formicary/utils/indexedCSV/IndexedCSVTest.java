package net.formicary.utils.indexedCSV;

import org.testng.annotations.Test;

import java.util.List;

@Test
public class IndexedCSVTest {
    @Test
    public void TabEOFWindowsEOLCSVBuilder() {
        List<List<String>> csv = CSVReader.get("/Users/slimemon/clearing/reports/DMPAUC00072 - Cash Flow and Trade Level NPV_ 1.TXT");
        int i = 0;
        for (List<String> line : csv) {
            for (String field : line) {
                i+=field.length();
            }
        }
    }
}
