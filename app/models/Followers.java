package models;

import com.avaje.ebean.Ebean;
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


    public static List<Followers> findAllFollowedByUser(Long id) {
        return finder.where().eq("userId", id).findList();
    }

    public static void unfollow(Long uid, Long erid) {
        Followers relation = Followers.finder.where().eq("userId", uid).eq("exchangeRateId", erid).findUnique();
        if (relation != null) {
            Ebean.delete(relation);
        }
    }

    public static boolean checkFollower(Long uid, Long erid) {
        Followers relation = finder.where().eq("userId", uid).eq("exchangeRateId", erid).findUnique();
        if (relation != null) {
            return true;
        } else {
            return false;
        }
    }
}
