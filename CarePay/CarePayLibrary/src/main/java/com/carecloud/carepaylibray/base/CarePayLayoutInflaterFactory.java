package com.carecloud.carepaylibray.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pjohnson on 14/03/17.
 */
public class CarePayLayoutInflaterFactory implements LayoutInflater.Factory2 {

    public static final String BREEZE_SCHEMA = "http://schemas.carecloud.com/breeze";
    public static final String BREEZE_ATTR_TEXT_KEY = "textKey";
    public static final String BREEZE_ATTR_HINT_KEY = "hintKey";
    private static final List<String> FILTER_TYPES = Arrays.asList(new String[]{
            "ViewStub", "View"
    });
    private static HashMap<String, Constructor<? extends View>> constructorMap = new HashMap<>();
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = new Class[]{Context.class, AttributeSet.class};

    private final LayoutInflater.Factory2 baseFactory;

    public CarePayLayoutInflaterFactory(LayoutInflater.Factory2 baseFactory) {
        super();
        this.baseFactory = baseFactory;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = baseFactory.onCreateView(parent, name, context, attrs);
        if (view == null) {
            view = createView(name, context, attrs);
        }
        applyAttributes(view, context, attrs);
        return view;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = baseFactory.onCreateView(name, context, attrs);
        if (view == null) {
            view = createView(name, context, attrs);
        }
        applyAttributes(view, context, attrs);
        return view;
    }

    private static void applyAttributes(View view, Context context, AttributeSet attrs) {
        if ((view != null) && (view instanceof TextView)) {
            // Text
            String textKey = attrs.getAttributeValue(BREEZE_SCHEMA, BREEZE_ATTR_TEXT_KEY);
            if (textKey != null) {
                ((TextView) view).setText(Label.getLabel(textKey));
            }
            String hintKey = attrs.getAttributeValue(BREEZE_SCHEMA, BREEZE_ATTR_HINT_KEY);
            if(hintKey != null){
                ((TextView) view).setHint(Label.getLabel(hintKey));
            }
        }
    }

    /**
     * Low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * factory's ClassLoader.
     * There are two things that can happen in an error case: either the
     * exception describing the error will be thrown, or a null will be
     * returned. You must deal with both possibilities -- the former will happen
     * the first time createView() is called for a class of a particular name,
     * the latter every time there-after for that class name.
     *
     * @param name  The full name of the class to be instantiated.
     * @param attrs The XML attributes supplied for this instance.
     * @return View The newly instantiated view, or null.
     * @see LayoutInflater#createView(String, String, AttributeSet)
     */
    protected final View createView(String name, Context context, AttributeSet attrs) {

        // Determine the qualified name of the view; if null, do not attempt to create a view
        String qualifiedName = asQualifiedName(name);
        if (qualifiedName == null) {
            return null;
        }

        Constructor<? extends View> constructor = constructorMap.get(qualifiedName);
        Class<? extends View> clazz = null;
        View view = null;

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = context.getClassLoader().loadClass(qualifiedName).asSubclass(View.class);

                constructor = clazz.getConstructor(CONSTRUCTOR_SIGNATURE);
                constructorMap.put(qualifiedName, constructor);
            }

            Object[] args = new Object[]{context, attrs};
            constructor.setAccessible(true);
            view = constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            Log.w("Breeze", String.format("%s: no suitable constructor for %s", attrs.getPositionDescription(), qualifiedName));
        } catch (ClassCastException e) {
            Log.w("Breeze", String.format("%s: class %s is not a view", attrs.getPositionDescription(), qualifiedName));
        } catch (ClassNotFoundException e) {
            Log.w("Breeze", String.format("%s: class %s not found", attrs.getPositionDescription(), qualifiedName));
        } catch (Exception e) {
            Log.w("Breeze", String.format("%s: general exception = %s", attrs.getPositionDescription(), qualifiedName));
        }
        return view;
    }

    protected static String asQualifiedName(String name) {
        // TCM: a bit of a hack, but based on similar internal logic from {@code LayoutInflater}
        // If the name is not package qualified, assume it is a base Android view
        String qualifiedName = name;
        if (name != null && (name.indexOf('.') < 0)) {

            // Look for known, unqualified 'special cases'.  For now, let them be resolved by other factories
            if (FILTER_TYPES.contains(name)) {
                return null;
            }

            qualifiedName = "android.widget." + name;
        }

        return qualifiedName;
    }
}
