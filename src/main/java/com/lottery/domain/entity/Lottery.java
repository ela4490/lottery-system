package com.lottery.domain.entity;

import com.lottery.domain.BallotUnit;
import com.lottery.domain.LotteryState;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lottery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    private Timestamp createDate;
    @NotEmpty
    private String award;
    @NotNull
    @PositiveOrZero
    private Long ballotPrice;
    @NotNull
    @Enumerated(EnumType.STRING)
    private BallotUnit ballotUnit;
    @NotNull
    @Enumerated(EnumType.STRING)
    private LotteryState state;

    private Lottery(Long id) {
        this.id = id;
    }

    public static Lottery build(Long id) {
        return new Lottery(id);
    }
}
