package cz.muni.fi.pv260.productfilter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.googlecode.catchexception.CatchException.verifyException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AtLeastNOfFilterTest<T> {

    @Mock
    Filter<T> filter1;
    @Mock
    Filter<T> filter2;
    @Mock
    Filter<T> filter3;
    @Mock
    Filter<T> filter4;
    @Mock
    Filter<T> filter5;

    @Mock
    T item1;
    @Mock
    T item2;
    @Mock
    T item3;

    @Before
    public void beforeTest() {

        when(filter1.passes(item1)).thenReturn(true);
        when(filter2.passes(item1)).thenReturn(true);
        when(filter3.passes(item1)).thenReturn(false);
        when(filter4.passes(item1)).thenReturn(true);
        when(filter5.passes(item1)).thenReturn(false);

        when(filter1.passes(item2)).thenReturn(false);
        when(filter2.passes(item2)).thenReturn(false);
        when(filter3.passes(item2)).thenReturn(false);
        when(filter4.passes(item2)).thenReturn(true);
        when(filter5.passes(item2)).thenReturn(false);

        when(filter1.passes(item3)).thenReturn(true);
        when(filter2.passes(item3)).thenReturn(false);
        when(filter3.passes(item3)).thenReturn(true);
        when(filter4.passes(item3)).thenReturn(true);
        when(filter5.passes(item3)).thenReturn(true);
    }

    @Test
    public void testExceptionInConstructor() throws Exception {

        verifyFilterNeverSucceedsExceptionThrown(4, filter1, filter2, filter3);

        verifyIllegalArgumentExceptionThrown(0, filter1, filter2);

        verifyIllegalArgumentExceptionThrown(-2, filter1, filter2);
    }


    private void verifyFilterNeverSucceedsExceptionThrown(int n, Filter<T>... filters) throws Exception {
        verifyException(() -> new AtLeastNOfFilter<T>(n, filters));
        assertThat((Exception) caughtException())
                .isInstanceOf(FilterNeverSucceeds.class);
    }

    private void verifyIllegalArgumentExceptionThrown(int n, Filter<T>... filters) throws Exception {
        verifyException(() -> new AtLeastNOfFilter<T>(n, filters));
        assertThat((Exception) caughtException())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAtLeastNChildFiltersPass() {
        AtLeastNOfFilter<T> testObject = new AtLeastNOfFilter<T>(3, filter1, filter2, filter3, filter4, filter5);

        Assert.assertEquals(testObject.passes(item1), true);
        Assert.assertEquals(testObject.passes(item2), false);
    }

    @Test
    public void testFilerFailsForNMinusOneChildFiltersPass() {
        AtLeastNOfFilter<T> testObject = new AtLeastNOfFilter<T>(4, filter1, filter2, filter3, filter4, filter5);

        Assert.assertEquals(testObject.passes(item1), false);
        Assert.assertEquals(testObject.passes(item3), true);
    }

}