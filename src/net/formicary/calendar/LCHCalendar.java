package net.formicary.calendar;


import com.mongodb.*;
import com.mongodb.util.JSON;
import org.apache.tools.ant.taskdefs.Java;
import org.jquantlib.time.Date;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LCHCalendar extends org.jquantlib.time.Calendar {
    private static final Map<String, Impl> calCache = new ConcurrentHashMap<String, Impl>();
    private final String name;

    public LCHCalendar(final String name) throws UnknownHostException {
        this.name = name;
        if (calCache.containsKey(name))
            impl = calCache.get(name);
        else {
            impl = new WesternImpl() {
                final List<Date> jdates = getFromMongoDB(name);

                @Override
                public String name() {
                    return LCHCalendar.this.name;
                }

                @Override
                public boolean isBusinessDay(Date date) {
                    return !isWeekend(date.weekday()) && !jdates.contains(date);
                }
            };
            calCache.put(name, impl);
        }
    }

    private List<Date> getFromMongoDB(String name) {
        try {
            DB lchdata = new Mongo().getDB("lchdata");
            DBCollection holidaysCollection = lchdata.getCollection("holidays");
            DBObject parse = (DBObject) JSON.parse("{_id:\"" + name + "\"}");
            final List<java.util.Date> dates = (List<java.util.Date>) holidaysCollection.findOne(parse).get("holidays");
            final List<Date> jdates = new ArrayList<Date>(dates.size());
            for (java.util.Date date : dates)
                jdates.add(new Date(date));
            return jdates;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
