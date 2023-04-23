package com.lottery.domain.entity;

import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"lottery_id", "ssn"}))
@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String ssn;
    @ManyToOne
    private Lottery lottery;
    @NotNull
    private Timestamp registrationDate;

    private Participant(String ssn, Lottery lottery) {
        this.ssn = ssn;
        this.lottery = lottery;
    }

    public static Participant build(String ssn, Lottery lottery) {
        return new Participant(ssn, lottery);
    }
}
