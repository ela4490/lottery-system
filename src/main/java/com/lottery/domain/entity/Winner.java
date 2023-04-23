package com.lottery.domain.entity;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Winner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Ballot ballot;
    @NotNull
    @Temporal(value = TemporalType.DATE)
    private Date date;

    private Winner(Ballot ballot, Date date) {
        this.ballot = ballot;
        this.date = date;
    }

    public static Winner build(Ballot ballot, Date date) {
        return new Winner(ballot, date);
    }

}
