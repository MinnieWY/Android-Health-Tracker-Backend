package com.wyminnie.healthtracker.base.dashboard;

import java.sql.Date;

import lombok.Data;

@Data
public class CompleteRecordDTO {
    String userId;
    Integer steps;
    Integer days;
    String startDate;
}
