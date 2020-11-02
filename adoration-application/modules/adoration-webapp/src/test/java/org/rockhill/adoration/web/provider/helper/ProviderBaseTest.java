package org.rockhill.adoration.web.provider.helper;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProviderBaseTest {

    private ProviderBase underTest;

    @BeforeMethod
    public void setUp() {
        underTest = Mockito.spy(new ProviderBase());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsLongChangedNullNull() {
        boolean result = underTest.isLongChanged(null, null);
        Assert.assertFalse(result);

    }

    @Test
    public void testIsLongChangedNullA() {
        Long a = 0L;
        boolean result = underTest.isLongChanged(null, a);
        Assert.assertTrue(result);

    }

    @Test
    public void testIsLongChangedANull() {
        Long a = 0L;
        boolean result = underTest.isLongChanged(a, null);
        Assert.assertTrue(result);
    }

    @Test
    public void testIsLongChangedSame() {
        Long a = 0L;
        Long b = 0L;
        boolean result = underTest.isLongChanged(a, b);
        Assert.assertFalse(result);
    }

    @Test
    public void testIsLongChangedDifferent() {
        Long a = 0L;
        Long b = 1L;
        boolean result = underTest.isLongChanged(a, b);
        Assert.assertTrue(result);
    }

}