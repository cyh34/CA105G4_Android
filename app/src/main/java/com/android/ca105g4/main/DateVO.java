package com.android.ca105g4.main;

import java.io.Serializable;

public class DateVO implements Serializable {

    private Long initMinDate = System.currentTimeMillis();
    private Long initMaxDate = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 30L;       // 要再加上更詳細的限制, 選了其中一個之後;

    private Long minDate;
    private Long maxDate;

    public Long getInitMinDate() {
        return initMinDate;
    }

    public Long getInitMaxDate() {
        return initMaxDate;
    }

    public Long getMinDate() {
        return minDate;
    }

    public void setMinDate(Long minDate) {
        this.minDate = minDate;
    }

    public Long getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Long maxDate) {
        this.maxDate = maxDate;
    }
}
