package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import play.data.validation.*;

/**
 * Created by Tomasz on 6/3/2015.
 */
@Entity
public class ExchangeRate extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(6)
    @Column(unique = true)
    public String name;

    public static Finder<Long, ExchangeRate> finder = new Finder<Long, ExchangeRate>(
            Long.class, ExchangeRate.class
    );

    public static ExchangeRate findByName(String name) {
        return finder.where().eq("name", name).findUnique();
    }
}
