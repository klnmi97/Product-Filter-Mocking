package cz.muni.fi.pv260.productfilter;

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


    @Before
    public void beforeTest() {
        
    }

    @Test
    public void testExceptionInConstructor() throws Exception {

        verifyFilterNeverSucceedsExceptionThrown(5, filter1, filter2, filter3);

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

    }


}