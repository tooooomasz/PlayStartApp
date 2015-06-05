package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mock.DBMock;
import mock.RateHistory;
import models.ExchangeRate;
import models.Followers;
import models.User;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.allRates;
import views.html.rate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Tomasz on 6/1/2015.
 */
public class CurrencyRates extends Controller {

    public static Result details(String name) {
        RateHistory rh = DBMock.getRateHistory(name);
        int size = rh.getValues().size();
        int daysBack = 10;
        List<Double> values = rh.getValues().subList(size-daysBack, size);
        List<String> labels = rh.getLabels().subList(size-daysBack, size);
        List<Double> changes = new ArrayList<Double>();

        List<Double> valsChan = rh.getValues().subList(size-daysBack-1, size);
        double change = 0;
        for (int i=0; i<valsChan.size(); i++) {
            if (i==0) {
                change = valsChan.get(i);
            } else {
                change = 100 * (valsChan.get(i) - valsChan.get(i-1)) / valsChan.get(i-1);
                changes.add(change);
            }
        }

        return ok(rate.render(name, values, labels, changes));
    }

    public static Result all() {
        List<String> names = new ArrayList<String>();
        List<Double> values = new ArrayList<Double>();
        List<Double> changes = new ArrayList<Double>();

        Set<String> rates = DBMock.getAllRateNames();

        for (String rate : rates) {
            RateHistory rh = DBMock.getRateHistory(rate);
            names.add(rh.getName());
            double last = rh.getValues().get(rh.getValues().size()-1);
            values.add(rh.getValues().get(rh.getValues().size()-1));
            double lastButOne = rh.getValues().get(rh.getValues().size()-2);
            double change = 100 * (last - lastButOne) / lastButOne;
            changes.add(change);
        }

        return ok(allRates.render(names, values, changes));
    }

//    @Security.Authenticated(Secured.class)
//    //@BodyParser.Of(BodyParser.Json.class)
//    public static Result my() {
//        ObjectNode result = Json.newObject();
//        result.put("test", "ok");
//        return ok(result);
//    }

//    @Security.Authenticated(Secured.class)
//    public static Result follow(String rateName) {
//        User user = User.findByEmail(request().username());
//        ExchangeRate exchangeRate = ExchangeRate.findByName(rateName);
//        Followers f = new Followers();
//        f.userId = user.id;
//        f.exchangeRateId = exchangeRate.id;
//        Ebean.save(f);
//        // TODO do zmiany
//        return all();
//    }
}
