package com.cpm.GetterSetter;

/**
 * Created by upendra on 05-03-2018.
 */

public class VisitorDetailGetterSetter {

    String In_time_img;
    String Out_time_img;
    String Emp_code;
    String Visit_date;
    String In_time;
    String Out_time;
    String Upload_status;
    String isexit;
    String feedback="";
    String feedback_cd="";
    String remark="";
    String rating="";
    String Areason;
    String key_id="";

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getAreason() {
        return Areason;
    }

    public void setAreason(String areason) {
        Areason = areason;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback_cd() {
        return feedback_cd;
    }

    public void setFeedback_cd(String feedback_cd) {
        this.feedback_cd = feedback_cd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsexit() {
        return isexit;
    }

    public void setIsexit(String isexit) {
        this.isexit = isexit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String username;

    private String name="";

    private Integer empId;

    private String designation="";

    public String getIn_time_img() {
        return In_time_img;
    }

    public void setIn_time_img(String in_time_img) {
        In_time_img = in_time_img;
    }

    public String getOut_time_img() {
        return Out_time_img;
    }

    public void setOut_time_img(String out_time_img) {
        Out_time_img = out_time_img;
    }

    public String getEmp_code() {
        return Emp_code;
    }

    public void setEmp_code(String emp_code) {
        Emp_code = emp_code;
    }

    public String getVisit_date() {
        return Visit_date;
    }

    public void setVisit_date(String visit_date) {
        Visit_date = visit_date;
    }

    public String getIn_time() {
        return In_time;
    }

    public void setIn_time(String in_time) {
        In_time = in_time;
    }

    public String getOut_time() {
        return Out_time;
    }

    public void setOut_time(String out_time) {
        Out_time = out_time;
    }

    public String getUpload_status() {
        return Upload_status;
    }

    public void setUpload_status(String upload_status) {
        Upload_status = upload_status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
