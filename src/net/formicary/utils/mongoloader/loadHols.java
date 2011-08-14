package net.formicary.utils.mongoloader;

import com.mongodb.*;
import net.formicary.utils.indexedCSV.CSVReader;
import org.bson.types.BasicBSONList;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class loadHols {
    public static void main(String[] args) throws UnknownHostException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        DB lchdata = new Mongo().getDB("lchdata");
        DBCollection holidaysCollection = lchdata.getCollection("holidays");
        holidaysCollection.drop();
        List<List<String>> hols = CSVReader.get(args[0]);
        Map<String, DBObject> holidays = new HashMap<String, DBObject>();
        boolean first = true;
        for (List<String> hol : hols) {
            if (first) {
                first = false;
                continue;
            }

            List<Object> holidayList;
            String id = hol.get(1);
            if (holidays.containsKey(id)) {
                holidayList = (List<Object>) holidays.get(id).get("holidays");
            } else {
                DBObject holidayObject = new BasicDBObject();
                holidayObject.put("_id", id);
                holidayList = new BasicDBList();
                holidayObject.put("holidays", holidayList);
                holidays.put(id, holidayObject);
            }
            String dateString = hol.get(2);
            Date date = sdf.parse(dateString);
            holidayList.add(date);
        }

        for (String key : holidays.keySet()) {
            holidaysCollection.insert(holidays.get(key));
        }
    }
}
