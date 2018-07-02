package com.example.georgekimutai.journalapp;

public class feelingposts {
    public String userid,date,time,feelingsdescription,postimage,profilepicture,fullnames;
    
    public feelingposts(){
        redirectusertopostfeelingactivity();
    }

    private void redirectusertopostfeelingactivity() {
    }

    public feelingposts(String userid, String date, String time, String feelingsdescription, String postimage, String profilepicture, String fullnames) {
        this.userid = userid;
        this.date = date;
        this.time = time;
        this.feelingsdescription = feelingsdescription;
        this.postimage = postimage;
        this.profilepicture = profilepicture;
        this.fullnames = fullnames;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFeelingsdescription() {
        return feelingsdescription;
    }

    public void setFeelingsdescription(String feelingsdescription) {
        this.feelingsdescription = feelingsdescription;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getFullnames() {
        return fullnames;
    }

    public void setFullnames(String fullnames) {
        this.fullnames = fullnames;
    }

}
