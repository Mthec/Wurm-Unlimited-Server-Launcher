package mod.wurmonline.serverlauncher;

import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleHelper {
    public static ResourceBundle getBundle(String bundleName) {
        try {
            File file = new File("locales");
            URL[] urls = new URL[] {file.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            return ResourceBundle.getBundle(bundleName, Locale.getDefault(), loader);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public static String getPluralString(ResourceBundle bundle, String string, int count) {
        return bundle.getString(string + PluralRules.forLocale(ULocale.getDefault()).select(count));
    }
}
