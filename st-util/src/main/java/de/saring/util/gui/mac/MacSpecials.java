package de.saring.util.gui.mac;

import java.awt.event.ActionEvent;
import java.awt.Image;
import java.lang.reflect.*;
import java.util.Locale;

import javax.swing.Action;

/**
 * Class to handle special features for java apps running on MacOS X.
 * 
 * For some functionalities reflections are used, so that this class is also
 * compilable on non Mac platforms, where package com.apple.eawt is not available.
 *
 * @author Mathias Obst
 * @version 1.0
 */
public final class MacSpecials {

    private Object eawtApp = null;
    private Action aboutAction = null;
    private Action prefsAction = null;
    private Action quitAction = null;

    /**
     * Holder class for Singleton pattern (Initialization on demand holder idiom)
     * see: http://de.wikipedia.org/wiki/Singleton_(Entwurfsmuster)#Eager_Creation
     */
    private static final class InstanceHolder {
        private static final MacSpecials INSTANCE = new MacSpecials();
    }

    /**
     * Private constructor to avoid external instanciation.
     */
    private MacSpecials() {
        try {
            //Dynamically get Mac application instance
            Class<?> eawtAppClass = Class.forName("com.apple.eawt.Application");
            Method getAppMethod = eawtAppClass.getDeclaredMethod("getApplication", (Class[]) null);
            eawtApp = getAppMethod.invoke(null, (Object[]) null);

            //Dynamically get Mac application listener interface and create a proxy instance for it
            Class<?> eawtAppListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            Object appListenerProxy = Proxy.newProxyInstance(AppListenerProxy.class.getClassLoader(),
                                                             new Class[] { eawtAppListenerClass },
                                                             new AppListenerProxy());
            
            //Dynamically add proxy instance as ApplicationListener
            Class<?> c = eawtApp.getClass();
            Method m = c.getDeclaredMethod("addApplicationListener", new Class[] { eawtAppListenerClass });
            m.invoke(eawtApp, new Object[] { appListenerProxy });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the single instance of the class.
     */
    public static MacSpecials getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Check if running operating system is MacOS X
     */
    public static boolean isMacOSX() {
        String os = System.getProperty("os.name").toUpperCase(Locale.getDefault());
        return os.startsWith("MAC OS X");
    }

    /**
     * Set the name of the Mac application menu.
     * Must be called as early as possible at the beginning of application initialization.
     * @param name
     */
    public static void setApplicationMenuName(String name) {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", name);
    }

    /**
     * Set if the application menus should be integrated into the global Mac menu bar.
     * Must be called as early as possible at the beginning of application initialization.
     */
    public static void useScreenMenuBar(boolean use) {
        System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(use));
    }

    /**
     * Set the dock icon.
     */
    public void setDockIcon(Image dockIcon) {
        try {
            //Dynamically call Mac applications method
            Class<?> c = eawtApp.getClass();
            Method m = c.getDeclaredMethod("setDockIconImage",  new Class[] { Image.class });
            m.invoke(eawtApp, new Object[] { dockIcon });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Enable or disable about menu item in the Mac application menu.
     */
    public void setAboutMenuItemEnabled(boolean enable) {
        try {
            //Dynamically call Mac applications method
            Class<?> c = eawtApp.getClass();
            Method m = c.getDeclaredMethod("setEnabledAboutMenu",  new Class[] { boolean.class });
            m.invoke(eawtApp, new Object[] { Boolean.valueOf(enable) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the action which should be executed on Mac application about activation.
     */
    public void setAboutAction(Action action) {
        aboutAction = action;
        if (aboutAction != null) {
            //Show and enable Mac application about menu item
            setAboutMenuItemEnabled(true);
        } else {
            //Disable Mac application about menu item
            setAboutMenuItemEnabled(false);
        }
    }

    /**
     * Enable or disable preferences menu item in the Mac application menu.
     */
    public void setPreferencesMenuItemEnabled(boolean enable) {
        try {
            //Dynamically call Mac applications method
            Class<?> c = eawtApp.getClass();
            Method m = c.getDeclaredMethod("setEnabledPreferencesMenu",  new Class[] { boolean.class });
            m.invoke(eawtApp, new Object[] { Boolean.valueOf(enable) });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the action which should be executed on Mac application preferences activation.
     */
    public void setPreferencesAction(Action action) {
        prefsAction = action;
        if (prefsAction != null) {
            //Show and enable Mac application preferences menu item
            setPreferencesMenuItemEnabled(true);
        } else {
            //Disable Mac application preferences menu item
            setPreferencesMenuItemEnabled(false);
        }
    }

    /**
     * Set the action which should be executed on Mac application quit activation.
     */
    public void setQuitAction(Action action) {
        quitAction = action;
    }

    /**
     * Proxy class to use as Mac ApplicationListener implementation.
     */
    public class AppListenerProxy implements InvocationHandler {

        /* ApplicationListener method calls arrive here */
        public Object invoke(Object proxy, Method method, Object[] args) {
            try {
                boolean eventHandled = true;
                String methodName = method.getName();
                if (aboutAction != null && methodName.equals("handleAbout")) {
                    //Execute given about action
                    aboutAction.actionPerformed(new ActionEvent(eawtApp, ActionEvent.ACTION_PERFORMED, ""));
                } else if (prefsAction != null && methodName.equals("handlePreferences")) {
                    //Execute given preferences action
                    prefsAction.actionPerformed(new ActionEvent(eawtApp, ActionEvent.ACTION_PERFORMED, ""));
                } else if (quitAction != null && methodName.equals("handleQuit")) {
                    //Execute given quit action
                    quitAction.actionPerformed(new ActionEvent(eawtApp, ActionEvent.ACTION_PERFORMED, ""));
                } else {
                    //No special handling here -> default handling will take place
                    eventHandled = false;
                }

                Object eawtAppEvent = args[0];
                if (eawtAppEvent != null) {
                    //Dynamically call setHandled method of Mac ApplicationEvent
                    Class<?> c = eawtAppEvent.getClass();
                    Method m = c.getDeclaredMethod("setHandled", new Class[] { boolean.class });
                    m.invoke(eawtAppEvent, new Object[] { Boolean.valueOf(eventHandled) });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Nothing to return here
            return null;
        }
    }
}
