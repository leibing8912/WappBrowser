package cn.ecpark.wappbrowser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_isurlvalid() throws Exception {
        assertTrue(Utils.isUrlValid("1://www.bai_du.com/sdfkjig/bi#?sdfisd=%sfd"));
    }

    @Test
    public void test_isstrempty() throws Exception {
        assertTrue(Utils.isStrEmpty("dadad"));
    }
}