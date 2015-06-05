package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mock.DBMock;
import mock.RateHistory;
import models.ExchangeRate;
import models.Followers;
import models.User;
import play.Logger;
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

        String email = ctx().session().get("email");
        if (email != null) {
            User user = User.findByEmail(email);
            ExchangeRate er = ExchangeRate.findByName(name);
            if (user != null && user.validated) {
                boolean follower = Followers.checkFollower(user.id, er.id);
                return ok(rate.render(name, values, labels, changes, user, follower));
            }
        }
        return ok(rate.render(name, values, labels, changes, null, null));
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

        String title = "All rates";
        String email = ctx().session().get("email");
        if (email != null) {
            User user = User.findByEmail(email);
            if (user != null && user.validated) {
                return ok(allRates.render(title, names, values, changes, user));
            }
        }
        return ok(allRates.render(title, names, values, changes, null));
    }

    @Security.Authenticated(Secured.class)
    public static Result my() {
        List<String> names = new ArrayList<String>();
        List<Double> values = new ArrayList<Double>();
        List<Double> changes = new ArrayList<Double>();

        User user = User.findByEmail(request().username());
        List<Followers> followed = Followers.findAllFollowedByUser(user.id);

        for (Followers f : followed) {
            String rate = ExchangeRate.findById(f.exchangeRateId).name;
            RateHistory rh = DBMock.getRateHistory(rate);
            names.add(rh.getName());
            double last = rh.getValues().get(rh.getValues().size()-1);
            values.add(rh.getValues().get(rh.getValues().size()-1));
            double lastButOne = rh.getValues().get(rh.getValues().size()-2);
            double change = 100 * (last - lastButOne) / lastButOne;
            changes.add(change);
        }

        String title = "Followed rates";
        return ok(allRates.render(title, names, values, changes, user));
    }

    @Security.Authenticated(Secured.class)
    public static Result follow(String rateName) {
        User user = User.findByEmail(request().username());
        ExchangeRate exchangeRate = ExchangeRate.findByName(rateName);
        if (Followers.checkFollower(user.id, exchangeRate.id)) {
            return details(rateName);
        }
        Followers f = new Followers();
        f.userId = user.id;
        f.exchangeRateId = exchangeRate.id;
        Ebean.save(f);
        return details(rateName);
    }

    @Security.Authenticated(Secured.class)
    public static Result unfollow(String rateName) {
        User user = User.findByEmail(request().username());
        ExchangeRate exchangeRate = ExchangeRate.findByName(rateName);
        Followers.unfollow(user.id, exchangeRate.id);
        return details(rateName);
    }
}
