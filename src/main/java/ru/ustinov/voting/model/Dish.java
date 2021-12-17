package ru.ustinov.voting.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.ustinov.voting.web.formatter.PriceFormatter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dish", uniqueConstraints = {@UniqueConstraint(columnNames =
        {"date", "name", "restaurant_id"}, name = "date_name_rest_unique_idx")},
        indexes = {@Index(columnList = "date, name", name = "date_name_idx")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Dish extends NamedEntity {

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;

    @Column(name = "price", nullable = false)
    @NotNull
    @JsonSerialize(using = PriceFormatter.class)
//    @JsonDeserialize(using = PriceDeserializer.class)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    @JsonIgnoreProperties({"dishes"})
    private Restaurant restaurant;

    public Dish(Integer id, String name, LocalDate date, BigDecimal price, Restaurant restaurant) {
        super(id, name);
        this.date = date;
        this.price = price;
        this.restaurant = restaurant;
    }

    public Dish(Integer id, String name, LocalDate date, BigDecimal price) {
        super(id, name);
        this.date = date;
        this.price = price;
    }

    public Dish(Dish dish) {
        super(dish.getId(), dish.getName());
        this.price = dish.price;
        this.date = dish.getDate();
    }

}

