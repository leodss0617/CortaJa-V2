package com.serhat.autosub.cortaja;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.serhat.autosub.R;
import com.serhat.autosub.ui.main.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class CortaJaHomeInstrumentedTest {
    @Rule public ActivityScenarioRule<MainActivity> activity = new ActivityScenarioRule<>(MainActivity.class);

    @Test public void launcherShowsCortajaHomeInPortuguese() {
        onView(withText("CortaJá")).check(matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        onView(withText("Transforme vídeos longos nos melhores momentos.")).check(matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
        onView(withText("+ NOVO PROJETO")).check(matches(androidx.test.espresso.matcher.ViewMatchers.isDisplayed()));
    }
}
