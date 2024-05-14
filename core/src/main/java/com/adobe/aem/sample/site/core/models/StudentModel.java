package com.adobe.aem.sample.site.core.models;

import com.adobe.aem.sample.site.core.Beans.StudentPojo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StudentModel {

    String firstname;
    String lastname;
    String phonenumber;
    String ScienceMarks;
    String MathsMarks;
    String HindiMarks;
    String SocialScienceMarks;

    long percentage;

    @ValueMapValue
    String select;
     private boolean pass=false;
    private boolean fail=false;
    public boolean isPass() {
        return pass;
    }
    public boolean isFail() {
        return fail;
    }


    public long getPercentage() {
        return percentage;
    }


    List<StudentPojo> list=new ArrayList<>();

    List<StudentPojo> passStudentList=new ArrayList<>();

    List<StudentPojo> failStudentList=new ArrayList<>();

    public List<StudentPojo> getPassStudentList() {
        return passStudentList;
    }

    public List<StudentPojo> getFailStudentList() {
        return failStudentList;
    }

    @SlingObject
    Resource resource;

    @ChildResource
                 @Named("FieldData")
    List<Resource> items;


    @PostConstruct
    protected void init()
    {
        if("pass".equals(select)){
            pass =true;
    }else if("Fail".equals(select)){
            fail =true;
        }
        if(items!=null)
        {
           for (int i=0;i<items.size();i++)
           {
               Resource resource = items.get(i);
               ValueMap valueMap = resource.getValueMap();
               String firstname = valueMap.get("firstname", "");
               String lastname = valueMap.get("lastname", "");
               String phonenumber = valueMap.get("phonenumber", "");
               String scienceMarks =valueMap.get("ScienceMarks","");
               String mathsMarks = valueMap.get("MathsMarks", "");
               String hindiMarks = valueMap.get("HindiMarks", "");
               String socialScienceMarks = valueMap.get("SocialScienceMarks", "");

               int i1 = Integer.parseInt(scienceMarks);
               int i3=Integer.parseInt(mathsMarks);
               int i4=Integer.parseInt(hindiMarks);
               int i2 = Integer.parseInt(socialScienceMarks);
               percentage=(i1+i2+i3+i4)/4;

               StudentPojo s=new StudentPojo();
               s.setPercentage(percentage);
               s.setFirstname(firstname);
               s.setLastname(lastname);
               s.setPhonenumber(phonenumber);
               s.setHindiMarks(hindiMarks);
               s.setMathsMarks(mathsMarks);
               s.setScienceMarks(scienceMarks);
               s.setSocialScienceMarks(socialScienceMarks);

               if(s.getPercentage()>=33)
               {
                   passStudentList.add(s);
               }
               else
               {
                   failStudentList.add(s);
               }




           }


        }
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getScienceMarks() {
        return ScienceMarks;
    }

    public String getMathsMarks() {
        return MathsMarks;
    }

    public String getHindiMarks() {
        return HindiMarks;
    }

    public String getSocialScienceMarks() {
        return SocialScienceMarks;
    }
    public List<StudentPojo> getList() {
        return list;
    }
}
