package com.bynder.lottery.controller.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created on 25/01/2023.
 *
 * @author Armen Aslikyan
 */
public class ApiResponseDto {

    private Long timeSpent;
    private String apiVersion;

    public ApiResponseDto() { }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApiResponseDto that = (ApiResponseDto) o;

        return new EqualsBuilder().append(timeSpent, that.timeSpent).append(apiVersion, that.apiVersion).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(timeSpent).append(apiVersion).toHashCode();
    }
}
