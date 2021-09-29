package ru.ustinov.voting.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
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
@Table(name = "vote", uniqueConstraints = {@UniqueConstraint(columnNames =
        {"date", "user_id"}, name = "date_user_unique_cts")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey=@ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL"))
    @JsonIgnoreProperties({"votes", "roles"})
    @ToString.Exclude
    private User user;


    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;


    public Vote(int id, Restaurant restaurant, User user, LocalDate date) {
        super(id);
        this.user = user;
        this.restaurant = restaurant;
        this.date = date;
    }

    public Vote(Restaurant restaurant, User user, LocalDate date) {
        this.restaurant = restaurant;
        this.user = user;
        this.date = date;
    }

}
