package org.rockhill.adoration.database.business;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BusinessWithAuditTrailTest {
    @InjectMocks
    private BusinessWithAuditTrail underTest;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCheckDangerousValueNoIssue() {
        //GIVEN
        //WHEN
        underTest.checkDangerousValue("normal text árvítűrőtükörfúrógép ÁRVÍZTŰRŐTÜKÖRFÚRÓGÉP [].;-_*=()+!%/",null);
        //THEN
        //nothing
    }

    @Test(expectedExceptions = DatabaseHandlingException.class)
    public void testCheckDangerousValueLeftBIssue() {
        //GIVEN
        //WHEN
        underTest.checkDangerousValue("hash is < need ",null);
        //THEN
        //exception shall occur
    }

    @Test(expectedExceptions = DatabaseHandlingException.class)
    public void testCheckDangerousValueRightBIssue() {
        //GIVEN
        //WHEN
        underTest.checkDangerousValue("hash is > need ",null);
        //THEN
        //exception shall occur
    }

    @Test(expectedExceptions = DatabaseHandlingException.class)
    public void testCheckDangerousValueBackSlIssue() {
        //GIVEN
        //WHEN
        underTest.checkDangerousValue("hash is \\ need ",null);
        //THEN
        //exception shall occur
    }

    @Test(expectedExceptions = DatabaseHandlingException.class)
    public void testCheckDangerousValueHashIssue() {
        //GIVEN
        //WHEN
        underTest.checkDangerousValue("hash is # need ",null);
        //THEN
        //exception shall occur
    }

    @Test(expectedExceptions = DatabaseHandlingException.class)
    public void testCheckDangerousValueAtIssue() {
        //GIVEN
        //WHEN
        underTest.checkDangerousValue("hash is & need ",null);
        //THEN
        //exception shall occur
    }


}