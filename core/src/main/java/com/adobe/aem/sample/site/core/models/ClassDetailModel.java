package com.adobe.aem.sample.site.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClassDetailModel {

    @ValueMapValue
    @Default(values = "")
    private String className;

    @ValueMapValue
    @Default(values = "")
    private String selectValue;

    @ValueMapValue
    @Default(values = "")
    private String studentName;

    @ValueMapValue
    @Default(values = "")
    @Named("fileReferenceStudent")
    private String studentImage;

    @ValueMapValue
    @Default(values = "")
    @Named("fileReferenceTeacher")
    private String teacherImage;


    @ValueMapValue
    @Default(values = "")
    private String rollNumber;

    @ValueMapValue
    @Default(values = "")
    private String teacherName;


    @ValueMapValue
    @Default(values = "")
    private String subject;


    @SlingObject
    private Resource resource;


    public String getClassName() {
        return className;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public String getStudentName() {
        return studentName;
    }


    public String getRollNumber() {
        return rollNumber;
    }

    public String getStudentImage() {
        return studentImage;
    }

    public String getTeacherImage() {
        return teacherImage;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getSubject() {
        return subject;
    }

    @PostConstruct
    protected void init() {
        if (StringUtils.isBlank(studentImage)) {
            Resource resourceStudentImage = resource.getChild("studentImage");
            if (resourceStudentImage != null) {
                studentImage = resourceStudentImage.getPath();
            }
        }
        if (StringUtils.isBlank(teacherImage)) {
            Resource resourceTeacherImage = resource.getChild("teacherImage");
            if (resourceTeacherImage != null) {
                teacherImage = resourceTeacherImage.getPath();
            }
        }
    }


}
