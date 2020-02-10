package com.cpm.GetterSetter;

import java.util.ArrayList;

/**
 * Created by upendra on 05-28-2018.
 */

public class VisitorSearchGetterSetter {

    ArrayList<String> EMP_CD = new ArrayList<>();
    ArrayList<String> EMPLOYEE = new ArrayList<>();
    ArrayList<String> DESIGNATION = new ArrayList<>();

    public ArrayList<String> getHR_LEGACY() {
        return HR_LEGACY;
    }

    public void setHR_LEGACY(String HR_LEGACY) {
        this.HR_LEGACY.add(HR_LEGACY);
    }

    ArrayList<String> HR_LEGACY = new ArrayList<>();

    String table_VISITOR_SEARCH;


    public ArrayList<String> getEMP_CD() {
        return EMP_CD;
    }

    public void setEMP_CD(String EMP_CD) {
        this.EMP_CD.add(EMP_CD);
    }

    public ArrayList<String> getEMPLOYEE() {
        return EMPLOYEE;
    }

    public void setEMPLOYEE(String EMPLOYEE) {
        this.EMPLOYEE.add(EMPLOYEE);
    }

    public ArrayList<String> getDESIGNATION() {
        return DESIGNATION;
    }

    public void setDESIGNATION(String DESIGNATION) {
        this.DESIGNATION.add(DESIGNATION);
    }

    public String getTable_VISITOR_SEARCH() {
        return table_VISITOR_SEARCH;
    }

    public void setTable_VISITOR_SEARCH(String table_VISITOR_SEARCH) {
        this.table_VISITOR_SEARCH = table_VISITOR_SEARCH;
    }
}
