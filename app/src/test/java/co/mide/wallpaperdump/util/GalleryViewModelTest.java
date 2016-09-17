package co.mide.wallpaperdump.util;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import co.mide.wallpaperdump.FakeData;
import co.mide.wallpaperdump.GalleryViewModel;
import rx.observers.TestSubscriber;

/**
 * Class to test GalleryViewModel
 */
@RunWith(JUnit4.class)
public class GalleryViewModelTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    GalleryViewModel galleryViewModel;

    @Before
    public void setUp() throws Exception {
        galleryViewModel = new GalleryViewModel(1, FakeData.createFakeDump(2));
    }

    @Test
    public void test_invalid_constructor_arguments() throws Exception {
        exception.expect(IllegalArgumentException.class);
        new GalleryViewModel(0, FakeData.createFakeDump(1));

        exception.expect(IllegalArgumentException.class);
        new GalleryViewModel(-1, FakeData.createFakeDump(1));

        exception.expect(IllegalArgumentException.class);
        new GalleryViewModel(1, null);

        exception.expect(IllegalArgumentException.class);
        new GalleryViewModel(-1, null);
    }

    @Test
    public void test_toggle_show_toolbar_observable_emits_true_first() throws Exception {

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();

        galleryViewModel.getToggleToolbarVisibilityObservable().subscribe(testSubscriber);

        testSubscriber.assertValues(true);
    }

    @Test
    public void test_toggle_show_toolbar_observable_alternates_values() throws Exception {

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();

        galleryViewModel.getToggleToolbarVisibilityObservable().subscribe(testSubscriber);
        galleryViewModel.toggleShowToolbar();
        galleryViewModel.toggleShowToolbar();
        galleryViewModel.toggleShowToolbar();

        testSubscriber.assertValues(true, false, true, false);
    }
}
