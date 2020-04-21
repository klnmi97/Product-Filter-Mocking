package cz.muni.fi.pv260.productfilter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
public class ControllerTest {

    @Mock
    private Output output;
    @Mock
    private Input input;
    @Mock
    private Logger logger;

    @Mock
    private Product productA;
    @Mock
    private Product productB;
    @Mock
    private Product productC;

    @Mock
    private Filter<Product> defaultFilter;
    @Mock
    private Filter<Product> filterPassProductB;
    @Mock
    private Filter<Product> filterPassAllProducts;
    @Mock
    private Filter<Product> filterPassNoProducts;

    private Collection<Product> allProducts;
    private List<Product> selectedProductsProductB;
    private List<Product> selectedNoProducts;
    private List<Product> selectedAllProducts;

    @Before
    public void beforeTest() throws ObtainFailedException {

        allProducts = Arrays.asList(productA, productB, productC);
        when(input.obtainProducts()).thenReturn(allProducts);

        when(filterPassProductB.passes(productA)).thenReturn(false);
        when(filterPassProductB.passes(productB)).thenReturn(true);
        when(filterPassProductB.passes(productC)).thenReturn(false);
        selectedProductsProductB = Collections.singletonList(productB);

        when(filterPassAllProducts.passes(productA)).thenReturn(true);
        when(filterPassAllProducts.passes(productB)).thenReturn(true);
        when(filterPassAllProducts.passes(productC)).thenReturn(true);
        selectedAllProducts = Arrays.asList(productA, productB, productC);

        when(filterPassNoProducts.passes(productA)).thenReturn(false);
        when(filterPassNoProducts.passes(productB)).thenReturn(false);
        when(filterPassNoProducts.passes(productC)).thenReturn(false);
        selectedNoProducts = Collections.emptyList();

    }

    @Test
    public void testExceptionInConstructor() throws Exception {

        verifyExceptionThrownByConstructor(null, output, logger);

        verifyExceptionThrownByConstructor(input, null, logger);

        verifyExceptionThrownByConstructor(input, output, null);

    }

    private void verifyExceptionThrownByConstructor(Input input, Output output, Logger logger) throws Exception {
        verifyException(() -> new Controller(input, output, logger));
        assertThat((Exception) caughtException())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(null);
    }

    @Test
    public void testControllerSendExactProductToOutput_differentProducts() {

        Controller testSubject = new Controller(input, output, logger);

        verifyOutputAndLog(testSubject, filterPassProductB, selectedProductsProductB, 1);

        verifyOutputAndLog(testSubject, filterPassAllProducts, selectedAllProducts, 2);

        verifyOutputAndLog(testSubject, filterPassNoProducts, selectedNoProducts, 3);

    }



    private void verifyOutputAndLog(Controller testSubject, Filter<Product> filterPassProductB, List<Product> selectedProductsProductB, int callOrder) {
        testSubject.select(filterPassProductB);
        verify(output).postSelectedProducts(selectedProductsProductB);
        verify(logger, times(callOrder)).setLevel("INFO");
        verify(logger).log(Controller.TAG_CONTROLLER, "Successfully selected " + selectedProductsProductB.size()
                + " out of " + allProducts.size() + " available products.");
    }


    @Test
    public void testControllerSendExactProductToOutput_threeSameProducts() throws ObtainFailedException {
        List<Product> InputOutputProducts = Arrays.asList(productB, productB, productB);
        when(input.obtainProducts()).thenReturn(InputOutputProducts);

        Controller testSubject = new Controller(input, output, logger);

        verifyOutputAndLog(testSubject, filterPassProductB, InputOutputProducts, 1);
    }

    @Test
    public void testControllerLogsExceptionWithoutPassingProductToOutput() throws ObtainFailedException {

        ObtainFailedException exceptionToBeThrown = mock(ObtainFailedException.class);
        when(input.obtainProducts()).thenThrow(exceptionToBeThrown);

        Controller testSubject = new Controller(input, output, logger);
        testSubject.select(defaultFilter);

        verify(output, times(0)).postSelectedProducts(anyCollectionOf(Product.class));
        verify(logger).setLevel("ERROR");
        verify(logger).log(Controller.TAG_CONTROLLER, "Filter procedure failed with exception: " + exceptionToBeThrown);

    }

}