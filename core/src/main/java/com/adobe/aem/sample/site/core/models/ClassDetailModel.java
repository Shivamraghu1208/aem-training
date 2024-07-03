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


/**
 * Sling Model class representing class details.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClassDetailModel {

    /**
     * The class name.
     */
    @ValueMapValue
    @Default(values = "")
    private String className;

    /**
     * The dropdown value.
     */
    @ValueMapValue
    @Default(values = "")
    private String dropdownValue;

    /**
     * The student name.
     */
    @ValueMapValue
    @Default(values = "")
    private String studentName;

    /**
     * The path to the student image.
     */
    @ValueMapValue
    @Default(values = "")
    @Named("fileReferenceStudent")
    private String studentImage;

    /**
     * The path to the teacher image.
     */
    @ValueMapValue
    @Default(values = "")
    @Named("fileReferenceTeacher")
    private String teacherImage;

    /**
     * The roll number.
     */
    @ValueMapValue
    @Default(values = "")
    private String rollNumber;

    /**
     * The teacher name.
     */
    @ValueMapValue
    @Default(values = "")
    private String teacherName;

    /**
     * The subject.
     */
    @ValueMapValue
    @Default(values = "")
    private String subject;

    /**
     * The resource - Resource Object
     */
    @SlingObject
    private Resource resource;

    /**
     * Retrieves the class name.
     *
     * @return The class name.
     */
    public String getClassName() {
        return className;
    }
    /**
     * Retrieves the dropdown value.
     *
     * @return The dropdown value.
     */
    public String getDropdownValue() {
        return dropdownValue;
    }
    /**
     * Retrieves the student's name.
     *
     * @return The student's name.
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Retrieves the roll number.
     *
     * @return The roll number.
     */
    public String getRollNumber() {
        return rollNumber;
    }
    /**
     * Retrieves the path to the student image.
     *
     * @return The path to the student image.
     */
    public String getStudentImage() {
        return studentImage;
    }
    /**
     * Retrieves the path to the teacher image.
     *
     * @return The path to the teacher image.
     */
    public String getTeacherImage() {
        return teacherImage;
    }
    /**
     * Retrieves the teacher's name.
     *
     * @return The teacher's name.
     */
    public String getTeacherName() {
        return teacherName;
    }
    /**
     * Retrieves the subject.
     *
     * @return The subject.
     */
    public String getSubject() {
        return subject;
    }


    /**
     * This method is automatically called by the Sling framework after the Sling Model object
     * is created and all dependencies are injected.
     * setting default values for images if not provided.
     */
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
