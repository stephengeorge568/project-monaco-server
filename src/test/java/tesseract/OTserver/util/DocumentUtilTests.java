package tesseract.OTserver.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tesseract.OTserver.objects.MonacoRange;
import tesseract.OTserver.objects.StringChangeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DocumentUtilTests {

    @Test
    void contextLoads() {

    }

    @Test
    void getIndex_startOfModel() {
        assertEquals(0, DocumentUtil.getIndex("hello", 1, 1));
    }

    @Test
    void getIndex_EndOfModel() {
        assertEquals(5, DocumentUtil.getIndex("hello", 6, 1));
    }

    @Test
    void getIndex_EndOfModel2() {
        assertEquals(1, DocumentUtil.getIndex("a", 2, 1));
    }

    @Test
    void getIndex_lines() {
        assertEquals(23, DocumentUtil.getIndex("hello\nthere\\n\nokay\n\n\nthree \\n above this", 3, 6));
    }

    @Test
    void getIndex_lines2() {
        assertEquals(10, DocumentUtil.getIndex("ok\nthen\\n\nsir", 1, 3));
    }

    @Test
    void updateModel_EmptySimpleInsert() {
        String model = "";
        String expectedModel = "abc";
        StringChangeRequest req = new StringChangeRequest("abc", new MonacoRange(1,1,1,1));

        assertEquals(expectedModel, DocumentUtil.updateModel(model, req));
    }

    @Test
    void updateModel_EmptySimpleInsert2() {
        String model = "";
        String expectedModel = "a";
        StringChangeRequest req = new StringChangeRequest("a", new MonacoRange(1,1,1,1));

        assertEquals(expectedModel, DocumentUtil.updateModel(model, req));
    }

    @Test
    void updateModel_EmptySimpleInsert3() {
        String model = "a";
        String expectedModel = "ab";
        StringChangeRequest req = new StringChangeRequest("b", new MonacoRange(2,2,1,1));

        assertEquals(expectedModel, DocumentUtil.updateModel(model, req));
    }

    @Test
    void updateModel_SelectionNoNewLine() {
        String model = "Stephen George Was Here";
        String expectedModel = "Stephen George Lived Here";
        StringChangeRequest req = new StringChangeRequest("Lived", new MonacoRange(16,19,1,1));

        assertEquals(expectedModel, DocumentUtil.updateModel(model, req));
    }

    @Test
    void updateModel_SelectionNewLines() {
        String model = "Stephen\nGeorge\nHas Was\nHere";
        String expectedModel = "Stephen\nGeorge\nHas Lived\nHere";
        StringChangeRequest req = new StringChangeRequest("Lived", new MonacoRange(5,8,3,3));

        assertEquals(expectedModel, DocumentUtil.updateModel(model, req));
    }
}
