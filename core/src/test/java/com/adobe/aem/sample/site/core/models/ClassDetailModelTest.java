package com.adobe.aem.sample.site.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith({AemContextExtension.class})
class ClassDetailModelTest {


    private final AemContext context=new AemContext();

    private ClassDetailModel classDetailModel;

    @Test
    void testClassDetail() {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/ClassDetailModel/resource.json", "/content/shivam/test");
        context.currentResource("/content/shivam/test");
        classDetailModel = context.request().adaptTo(ClassDetailModel.class);
        assertNotNull(classDetailModel);
        assertEquals("Java Class",classDetailModel.getClassName());
        assertEquals("valueStudent",classDetailModel.getDropdownValue());
        assertEquals("Shivam Raghuwanshi",classDetailModel.getStudentName());
        assertEquals("12345",classDetailModel.getRollNumber());
        assertEquals("/content/shivam/test/studentImage",classDetailModel.getStudentImage());
        assertEquals("/content/shivam/test/teacherImage",classDetailModel.getTeacherImage());
        assertEquals("Samantha",classDetailModel.getTeacherName());
        assertEquals("Java",classDetailModel.getSubject());
    }

    @Test
    void testWithStudent()
    {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/ClassDetailModel/resource1.json", "/content/shivam/test");
        context.currentResource("/content/shivam/test");
        classDetailModel = context.request().adaptTo(ClassDetailModel.class);
        assertNotNull(classDetailModel);
        assertEquals("Java Class",classDetailModel.getClassName());
        assertEquals("valueStudent",classDetailModel.getDropdownValue());
        assertEquals("Shivam Raghuwanshi",classDetailModel.getStudentName());
        assertEquals("/content/dam/core-components-examples/library/sample-assets/lava-rock-formation.jpg",classDetailModel.getStudentImage());
        assertEquals("12345",classDetailModel.getRollNumber());
        assertEquals("",classDetailModel.getTeacherName());
        assertEquals("",classDetailModel.getTeacherImage());
        assertEquals("",classDetailModel.getSubject());
    }
    @Test
    void testWithTeacher()
    {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/ClassDetailModel/resource2.json", "/content/shivam/test");
        context.currentResource("/content/shivam/test");
        classDetailModel = context.request().adaptTo(ClassDetailModel.class);
        assertNotNull(classDetailModel);
        assertEquals("AEM",classDetailModel.getClassName());
        assertEquals("valueTeacher",classDetailModel.getDropdownValue());
        assertEquals("",classDetailModel.getStudentName());
        assertEquals("",classDetailModel.getStudentImage());
        assertEquals("",classDetailModel.getRollNumber());
        assertEquals("Samantha",classDetailModel.getTeacherName());
        assertEquals("/content/shivam/test/teacherImage",classDetailModel.getTeacherImage());
        assertEquals("AEM",classDetailModel.getSubject());
    }
    @Test
    void testWithNullValue()
    {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/ClassDetailModel/resource3.json", "/content/shivam/test");
        context.currentResource("/content/shivam/test");
        classDetailModel = context.request().adaptTo(ClassDetailModel.class);
        assertNotNull(classDetailModel);
        assertEquals("",classDetailModel.getClassName());
        assertEquals("valueStudent",classDetailModel.getDropdownValue());
        assertEquals("",classDetailModel.getStudentName());
        assertEquals("",classDetailModel.getStudentImage());
        assertEquals("",classDetailModel.getRollNumber());
    }
}