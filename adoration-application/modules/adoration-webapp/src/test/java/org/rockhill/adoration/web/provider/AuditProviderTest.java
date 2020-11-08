package org.rockhill.adoration.web.provider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.web.helper.DummyTestObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

public class AuditProviderTest {

    @Mock
    private BusinessWithAuditTrail businessWithAuditTrail;

    @InjectMocks
    private AuditProvider underTest;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(underTest, "businessWithAuditTrail", businessWithAuditTrail);

    }

    @Test
    public void getAuditTrailOfLastDays() {
        //given
        List<DummyTestObject> expected = new ArrayList<>();
        doReturn(expected).when(businessWithAuditTrail).getAuditTrailOfLastDays(1L);
        //when
        Object result = underTest.getAuditTrailOfLastDays(1L);
        //then
        assertEquals(expected, result);
    }
}