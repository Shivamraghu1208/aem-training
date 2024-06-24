package com.adobe.aem.sample.site.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith({AemContextExtension.class})
class ClassDetailModelTest {


    private final AemContext context=new AemContext();

    private ClassDetailModel classDetailModel;

    @BeforeEach
    void setUp() {
        context.load(true).json("/com/adobe/aem/sample/site/core/models/ClassDetailModel/resource.json", "/content/shivam/test");
        context.currentResource("/content/shivam/test");
         classDetailModel = context.request().adaptTo(ClassDetailModel.class);
         assertNotNull(classDetailModel);

    }
    @Test
    void getClassName() {
        assertEquals("Java Class",classDetailModel.getClassName());
    }

    @Test
    void getDropdownValue() {
        assertEquals("valueStudent",classDetailModel.getDropdownValue());

    }

    @Test
    void getStudentName() {
        assertEquals("Shivam Raghuwanshi",classDetailModel.getStudentName());
    }

    @Test
    void getRollNumber() {
        assertEquals("12345",classDetailModel.getRollNumber());
    }

    @Test
    void getStudentImage() {
        assertEquals("/content/shivam/test/studentImage",classDetailModel.getStudentImage());
    }

    @Test
    void getTeacherImage() {
        assertEquals("/content/shivam/test/teacherImage",classDetailModel.getTeacherImage());
    }

    @Test
    void getTeacherName() {
        assertEquals("Samantha",classDetailModel.getTeacherName());
    }

    @Test
    void getSubject() {
        assertEquals("Java",classDetailModel.getSubject());
    }
}