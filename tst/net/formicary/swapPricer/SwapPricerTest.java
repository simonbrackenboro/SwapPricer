package net.formicary.swapPricer;

import com.mongodb.*;
import com.mongodb.util.JSON;
import net.formicary.calendar.LCHCalendar;
import org.jquantlib.time.*;
import org.jquantlib.time.calendars.JointCalendar;
import org.jquantlib.time.calendars.NullCalendar;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Test
public class SwapPricerTest {
    private final SimpleDateFormat ddMMyyyyhhmmss = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private final SimpleDateFormat yyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

    private final DB db;
    private final DBCollection fpmls;
    private final DBCollection cashflows;

    public SwapPricerTest() throws UnknownHostException {
        this.db = new Mongo().getDB("lchdata");
        this.fpmls = db.getCollection("fpml");
        this.cashflows = db.getCollection("cashflows");
    }

    @Test
    public void checkDate() throws UnknownHostException, ParseException {
        Date parse = ddMMyyyyhhmmss.parse("01/01/2072 00:00:00");
        Assert.assertFalse(new LCHCalendar("NZAU").isBusinessDay(new org.jquantlib.time.Date(parse)));
    }

    @DataProvider(name = "ids")
    public Object[][] generateTradeIds() {
        ArrayList<Object[]> objects = new ArrayList<Object[]>();
        DBObject query = (DBObject) JSON.parse("{}");
        DBObject filter = (DBObject) JSON.parse("{_id:1}");

        DBCursor cursor = fpmls.find(query, filter);

        while (cursor.hasNext()) {
            DBObject id = cursor.next();
            objects.add(new Object[]{id.get("_id")});
        }

        return objects.toArray(new Object[][]{});
    }

    @Test(dataProvider = "ids")
    public void generateSchedule(String id) throws ParseException, UnknownHostException {
        DBObject query = (DBObject) JSON.parse("{_id:\"" + id + "\" }");
        DBObject filter = (DBObject) JSON.parse("{" +
                "\"FpML.trade.swap.swapStream.calculationPeriodDates.effectiveDate\":1," +
                "\"FpML.trade.swap.swapStream.calculationPeriodDates.terminationDate\":1," +
                "\"FpML.trade.swap.swapStream.calculationPeriodDates.calculationPeriodFrequency\":1," +
                "\"FpML.trade.swap.swapStream.calculationPeriodDates.calculationPeriodDatesAdjustments\":1," +
                "}");

        DBCursor dbCursor = fpmls.find(query, filter);
        DBObject dates = dbCursor.next();
        DBObject stream1 = get(dates, "FpML.trade.swap.swapStream.0.calculationPeriodDates");

        List<org.jquantlib.time.Date> dates11 = generateCouponDates(stream1);
        List<Date> dates12 = (List<Date>) cashflows.findOne(query).get("P");

        for (Date date : dates12)
            Assert.assertTrue(dates11.contains(new org.jquantlib.time.Date(date)));

        DBObject stream2 = get(dates, "FpML.trade.swap.swapStream.1.calculationPeriodDates");
        List<org.jquantlib.time.Date> dates21 = generateCouponDates(stream2);
        List<Date> dates22 = (List<Date>) cashflows.findOne(query).get("R");

        for (Date date : dates22)
            Assert.assertTrue(dates21.contains(new org.jquantlib.time.Date(date)));
    }

    private List<org.jquantlib.time.Date> generateCouponDates(DBObject stream1) throws ParseException, UnknownHostException {
        org.jquantlib.time.Date effectiveDate = getDate(stream1, "effectiveDate.unadjustedDate.$");
        org.jquantlib.time.Date terminationDate = getDate(stream1, "terminationDate.unadjustedDate.$");
        TimeUnit timeUnit = getTimeUnit(stream1, "calculationPeriodFrequency.period.$");
        int freq = (Integer) get(stream1, "calculationPeriodFrequency.periodMultiplier.$");

        Object roll = get(stream1, "calculationPeriodFrequency.rollConvention.$");

        String businessDayConvention = get(stream1, "calculationPeriodDatesAdjustments.businessDayConvention.$");
        String businessCentersHref = get(stream1, "calculationPeriodDatesAdjustments.businessCentersReference.@href");
        Calendar c = getCalendar(stream1, "terminationDate.dateAdjustments.businessCenters.businessCenter");
        Period period = new Period(freq, timeUnit);

        final Calendar nullCalendar = new NullCalendar();

        MakeSchedule mSchedule = new MakeSchedule(effectiveDate, terminationDate, period, c, BusinessDayConvention.ModifiedFollowing);
        mSchedule.forwards();

        if (roll instanceof Integer) {
            org.jquantlib.time.Date firstDate = nullCalendar.advance(effectiveDate, period, BusinessDayConvention.Unadjusted, false);
            if (firstDate.dayOfMonth() != (Integer) roll) {
                Date tmp = firstDate.longDate();
                tmp.setDate((Integer) roll);
                firstDate = new org.jquantlib.time.Date(tmp);
                mSchedule.withFirstDate(firstDate);
            }
        }

        List<org.jquantlib.time.Date> dates = mSchedule.schedule().dates();
        return dates;
    }

    public static <T> T get(final DBObject dbo, final String key) {
        final String[] keys = key.split("\\.");
        DBObject current = dbo;
        Object result = null;
        for (int i = 0; i < keys.length; i++) {
            result = current.get(keys[i]);
            if (i + 1 < keys.length) {
                current = (DBObject) result;
            }
        }
        return (T) result;
    }

    public org.jquantlib.time.Date getDate(final DBObject dbo, final String key) throws ParseException {
        String s = get(dbo, key);
        return new org.jquantlib.time.Date(yyyMMdd.parse(s));
    }

    public Calendar getCalendar(final DBObject dbo, final String key) throws UnknownHostException {
        Object obj = get(dbo, key);
        List<DBObject> cals;
        if (obj instanceof BasicDBList)
            cals = (List<DBObject>) obj;
        else {
            cals = new ArrayList<DBObject>();
            cals.add((DBObject) obj);
        }
        Calendar[] rcals = new Calendar[cals.size()];
        int i = 0;
        for (DBObject cal : cals) {
            String $ = (String) cal.get("$");
            rcals[i++] = new LCHCalendar($);
        }
        return new JointCalendar(JointCalendar.JointCalendarRule.JoinHolidays, rcals);
    }

    public TimeUnit getTimeUnit(final DBObject dbo, final String key) throws ParseException {
        String s = get(dbo, key);
        if ("M".equals(s)) {
            return TimeUnit.Months;
        } else if ("Y".equals(s)) {
            return TimeUnit.Years;
        } else if ("T".equals(s))
            return TimeUnit.Months;
        throw new NotImplementedException();
    }


}
