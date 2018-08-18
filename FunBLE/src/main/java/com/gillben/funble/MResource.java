package com.gillben.funble;

import android.content.Context;

public class MResource {
    public static int getIdByName(Context context, String className, String name) {
        String packageName = context.getPackageName();
        Class r;
        int id = 0;
        try {
            r = Class.forName(packageName + ".R");

            Class[] classes = r.getClasses();
            Class desireClass = null;
            for (Class aClass : classes) {
                if (aClass.getName().split("\\$")[1].equals(className)) {
                    desireClass = aClass;
                    break;
                }
            }
            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return id;
    }
}
