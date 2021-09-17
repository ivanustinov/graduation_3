package ru.javaops.topjava.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 01.09.2021
 */

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)

//    @JsonBackReference(value = "rest_votes")

    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey=@ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL"))
    @JsonIgnoreProperties({"votes", "roles"})
    private User user;


    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;


    public Vote(int id, Restaurant restaurant, LocalDate date) {
        super(id);
        this.restaurant = restaurant;
        this.date = date;
    }
    public Vote(int id, Restaurant restaurant, User user, LocalDate date) {
        super(id);
        this.user = user;
        this.restaurant = restaurant;
        this.date = date;
    }
    public Vote(int id, User user, LocalDate date) {
        super(id);
        this.user = user;
        this.date = date;
    }
    public Vote(Restaurant restaurant, User user, LocalDate date) {
        this.restaurant = restaurant;
        this.user = user;
        this.date = date;
    }

}
