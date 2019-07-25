package org.rockhill.adorApp.database.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "adoration.dbo.hour")
public class Hours {

    private Long id;
    private Integer hour;
    private Integer weekday;
    private String dayText_en;
    private String dayText_hu;

    public Hours() {
        // this form used by Hibernate
    }

    @Column(name = "id", nullable = false)
    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "hour", nullable = false)
    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    @Column(name = "weekday", nullable = false)
    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    @Column(name = "dayText_en", nullable = false)
    public String getDayText_en() {
        return dayText_en;
    }

    public void setDayText_en(String dayText_en) {
        this.dayText_en = dayText_en;
    }

    @Column(name = "dayText_hu", nullable = false)
    public String getDayText_hu() {
            return dayText_hu;
    }

    public void setDayText_hu(String dayText_hu) {
        this.dayText_hu = dayText_hu;
    }

}
