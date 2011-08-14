package net.formicary.utils.mongoloader;

import com.mongodb.*;
import net.formicary.utils.indexedCSV.CSVReader;
import org.apache.ivy.core.search.OrganisationEntry;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class loadCashflows {
    public static void main(String[] args) throws UnknownHostException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        DB lchdata = new Mongo().getDB("lchdata");
        DBCollection cashflowsCollection = lchdata.getCollection("cashflows");
        cashflowsCollection.drop();
        List<List<String>> cashflows = CSVReader.get(args[0]);
        Map<String, DBObject> docs = new HashMap<String, DBObject>();
        boolean first = true;
        for (List<String> cashflow : cashflows) {
            if (first) {
                first = false;
                continue;
            }
            DBObject cashflowDoc;
            String id = cashflow.get(3);
            String PorR = cashflow.get(5);
            if (docs.containsKey(id)) {
                cashflowDoc = (DBObject) docs.get(id);
            } else {
                cashflowDoc = new BasicDBObject();
                cashflowDoc.put("_id", id);
                BasicDBList P = new BasicDBList();
                BasicDBList R = new BasicDBList();
                cashflowDoc.put("P", P);
                cashflowDoc.put("R", R);
                docs.put(id, cashflowDoc);
            }
            String dateString = cashflow.get(4);
            Date date = sdf.parse(dateString);
            ((BasicDBList)cashflowDoc.get(PorR)).add(date);
        }

        for (String key : docs.keySet()) {
            cashflowsCollection.insert(docs.get(key));
        }
    }
}
