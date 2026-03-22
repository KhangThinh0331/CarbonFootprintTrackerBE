package com.khangthinh.carbonfootprinttracker.repository;

import java.sql.Date;

public interface DailyCo2Projection {
    Date getLogDate();     // Ép kiểu Date từ DB
    Double getTotalCo2();  // Tổng SUM CO2
}
