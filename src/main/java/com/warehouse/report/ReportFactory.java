package com.warehouse.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReportFactory {
    private final Map<String, Report> reportMap;

    @Autowired
    public ReportFactory(List<Report> reports) {
        this.reportMap = reports.stream()
                .collect(Collectors.toMap(Report::getType, Function.identity()));
    }

    public Report getReport(String type) {
        return reportMap.get(type);
    }
} 