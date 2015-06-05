package mock;

import com.avaje.ebean.Ebean;
import models.ExchangeRate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tomasz on 6/1/2015.
 */
public class DBMock {

    private static Map<String, RateHistory> db;

    static {
        int daysBack = 100;
        db = new HashMap<String, RateHistory>();
        List<String> rates = new ArrayList<String>();
        rates.add("PLNEUR");
        rates.add("PLNUSD");
        rates.add("PLNGBP");
        rates.add("PLNCHF");
        rates.add("USDEUR");
        rates.add("EURCHF");
        rates.add("USDGBP");
        rates.add("EURGBP");

        for (String rate : rates) {
            db.put(rate, generateRateHistory(rate, daysBack));
        }

        if (ExchangeRate.finder.all().size() == 0) {
            for (String rate : rates) {
                ExchangeRate er1 = new ExchangeRate();
                er1.name = rate;
                Ebean.save(er1);
            }
        }
//        System.out.println(ExchangeRate.finder.all().size() + " rekordy w bazie.");
    }

    private static RateHistory generateRateHistory(String name, int daysBack) {
        List<Double> values = new ArrayList<Double>();
        List<String> dates = new ArrayList<String>();

        Random generator = new Random();
        double init = generator.nextInt(7) + Math.random();

        Date end = new Date();
        Date start = new Date(end.getTime() - daysToMilliseconds(daysBack));

        DateFormat df = new SimpleDateFormat("MM.dd.yyyy");

        for (int i=0; i<daysBack; i++) {
            Date date = new Date(start.getTime() + daysToMilliseconds(i));
            if (date.getTime() >= end.getTime()) {
                break;
            }
            double diff = Math.random() * 0.1 - 0.05;
            if (i == 0) {
                values.add(init);
            } else {
                if (values.get(i-1) + diff > 0) {
                    values.add(values.get(i-1) + diff);
                } else {
                    values.add(values.get(i-1) - diff);
                }
            }
            dates.add(df.format(date));
        }

        return new RateHistory(name, values, dates);
    }

    public static RateHistory getRateHistory(String name) {
        return db.get(name);
    }

    public static Set<String> getAllRateNames() {
        return db.keySet();
    }

    public static long daysToMilliseconds(int days) {
        return days * 86400000L;
    }
}