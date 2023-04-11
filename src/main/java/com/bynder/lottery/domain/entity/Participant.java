package com.bynder.lottery.domain.entity;

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
import java.sql.Timestamp;

@Setter
@Getter
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(ssn, that.ssn)
                .append(lottery, that.lottery)
                .append(registrationDate, that.registrationDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(ssn)
                .append(lottery)
                .append(registrationDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("ssn", ssn)
                .append("lottery", lottery)
                .append("registrationDate", registrationDate)
                .toString();
    }
}
