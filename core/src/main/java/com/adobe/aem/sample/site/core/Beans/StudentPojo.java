package com.adobe.aem.sample.site.core.Beans;

import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

public class StudentPojo {

    String firstname;

    String lastname;

    String phonenumber;

    String ScienceMarks;

    String MathsMarks;

    String HindiMarks;

    String SocialScienceMarks;

    long percentage;

    public long getPercentage() {
        return percentage;
    }

    public void setPercentage(long percentage) {
        this.percentage = percentage;
    }



    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getScienceMarks() {
        return ScienceMarks;
    }

    public void setScienceMarks(String scienceMarks) {
        ScienceMarks = scienceMarks;
    }

    public String getMathsMarks() {
        return MathsMarks;
    }

    public void setMathsMarks(String mathsMarks) {
        MathsMarks = mathsMarks;
    }

    public String getHindiMarks() {
        return HindiMarks;
    }

    public void setHindiMarks(String hindiMarks) {
        HindiMarks = hindiMarks;
    }

    public String getSocialScienceMarks() {
        return SocialScienceMarks;
    }

    public void setSocialScienceMarks(String socialScienceMarks) {
        SocialScienceMarks = socialScienceMarks;
    }

}
