package com.bynder.lottery.domain.entity;

import com.bynder.lottery.domain.BallotUnit;
import com.bynder.lottery.domain.LotteryState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;

@Setter
@Getter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Lottery that = (Lottery) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(createDate, that.createDate)
                .append(award, that.award)
                .append(ballotPrice, that.ballotPrice)
                .append(ballotUnit, that.ballotUnit)
                .append(state, that.state)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(createDate)
                .append(award)
                .append(ballotPrice)
                .append(ballotUnit)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("createDate", createDate)
                .append("award", award)
                .append("ballotPrice", ballotPrice)
                .append("ballotUnit", ballotUnit)
                .append("state", state)
                .toString();
    }
}
