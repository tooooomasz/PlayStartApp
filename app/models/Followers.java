package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by Tomasz on 6/3/2015.
 */
@Entity
public class Followers extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public Long userId;

    @Constraints.Required
    public Long exchangeRateId;

    public static Finder<Long, Followers> finder = new Finder<Long, Followers>(
            Long.class, Followers.class
    );


    public static List<Followers> findAllFollowedByUser(String name) {
        return finder.where().eq("name", name).findList();
    }
}
