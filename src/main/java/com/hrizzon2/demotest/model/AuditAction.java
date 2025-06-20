package com.hrizzon2.demotest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

// classe embarquée MongoDB

@Getter
@Setter
public class AuditAction {

    private String action;
    private Date date;
    private String par;
    // getters/setters
}
