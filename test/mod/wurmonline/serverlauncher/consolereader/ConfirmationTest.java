package mod.wurmonline.serverlauncher.consolereader;

import mod.wurmonline.serverlauncher.consolereader.confirmation.Confirmation;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationCallback;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationEnd;
import mod.wurmonline.serverlauncher.consolereader.confirmation.ConfirmationRequired;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConfirmationTest {
    String text = "Confirmation text";
    String yes = "yes";
    String no = "no";

    @Test(expected = ConfirmationEnd.class)
    public void testActionHasKey() throws Exception {
        ConfirmationCallback callback = mock(ConfirmationCallback.class);
        Mockito.doThrow(new ConfirmationEnd("")).when(callback).call();
        Map<String, ConfirmationCallback> answers = new HashMap<>();
        answers.put(yes, callback);
        Confirmation confirmation = new Confirmation(text, answers);
        confirmation.action(yes);
    }

    @Test
    public void testActionNotHaveKey() throws Exception {
        ConfirmationCallback callback = mock(ConfirmationCallback.class);
        Mockito.doThrow(new ConfirmationEnd("")).when(callback).call();
        Map<String, ConfirmationCallback> answers = new HashMap<>();
        answers.put(yes, callback);
        Confirmation confirmation = new Confirmation(text, answers);
        confirmation.action(no);
        // TODO - Shouldn't it test something?  Can you test the absence of anything?
    }

    @Test
    public void testGetText() throws Exception {
        try {
            throw new ConfirmationRequired(text, null);
        } catch (ConfirmationRequired confirmation) {
            assertTrue(confirmation.confirmation.getText().equals(text));
        }
    }

    @Test
    public void testConfirmationCallback() throws Exception {
        ConfirmationCallback callback = mock(ConfirmationCallback.class);
        Map<String, ConfirmationCallback> answers = new HashMap<>();
        answers.put(yes, callback);
        Confirmation confirmation = new Confirmation(text, answers);
        confirmation.action(yes);
        verify(callback).call();
    }

    // TODO - More tests.
}