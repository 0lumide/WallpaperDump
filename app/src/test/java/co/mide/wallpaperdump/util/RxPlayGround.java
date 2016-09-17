package co.mide.wallpaperdump.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;

/**
 * Class where I run random Rx related code to help in understanding the behaviour
 */
@RunWith(JUnit4.class)
public class RxPlayGround {

    @Test
    public void test1() throws Exception {
        BehaviorSubject<Boolean> mSelectedLanguage = BehaviorSubject.create(true);

        TestSubscriber<Boolean> testSubscriber = new TestSubscriber<>();

        mSelectedLanguage.subscribe(testSubscriber);
        mSelectedLanguage.onNext(false);

        testSubscriber.assertValues(true, false);
    }
}
