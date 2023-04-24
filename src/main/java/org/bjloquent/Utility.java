/*
 * The MIT License
 *
 * Copyright 2018 Derick Felix.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.bjloquent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author derickfelix
 * @date Feb 24, 2018
 */
public class Utility {

    public static String tableOf(Object obj) {
        return obj.getClass().getSimpleName().toLowerCase() + "s";
    }

    /**
     * Gets a list of fields containing a name and its value
     *
     * @param methods a array of methods of a object
     * @param invoker the object that is invoking each method
     * @param request whether it is request mode or not, if false, fields with
     *                null values will not be included
     * @param primaryKey the primary key of the table
     * @return a list of fields
     */
    public static List<Field> getFields(Method[] methods, Object invoker, boolean request, Object primaryKey) {
        boolean isCompositePrimaryKey = primaryKey instanceof String[];

        List<Field> fields = new ArrayList<>();
        try {
            for (int i = methods.length - 1; i >= 0; i--) {
                if (methods[i].getName().contains("get")) {
                    String name = toFieldName(methods[i].getName());
                    Object value = methods[i].invoke(invoker, (Object[]) null);
                    Class<?> type = methods[i].getReturnType();

                    if (request || value != null) {
                        if(isCompositePrimaryKey) {
                            String[] keys = (String[]) primaryKey;
                            boolean isPrimaryKey = false;
                            for (String key : keys) {
                                if (name.equals(key)) {
                                    isPrimaryKey = true;
                                    break;
                                }
                            }
                            if (isPrimaryKey) {
                                fields.add(0, new Field(name, value, type, true));
                            } else {
                                fields.add(new Field(name, value, type));
                            }
                        } else {
                            if (name.equals(primaryKey)) {
                                fields.add(0, new Field(name, value, type, true));
                            } else {
                                fields.add(new Field(name, value, type));
                            }
                        }
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            Logger.getLogger(Utility.class.getName()).log(Level.SEVERE, "Failed to get name of fields", e);
        }

        return fields;
    }

    /**
     * Removes the 'get' prefix of string and set its first character to lower
     * case, for example a string with <code>getAddress</code> as its value will
     * return <code>address</code>.
     *
     * @param getterMethod a string with the 'get' prefix.
     * @return a string without the 'get' prefix and its first character to
     * lower case.
     */
    private static String toFieldName(String getterMethod) {
        // eliminate get** Prefix
        char[] field = getterMethod.substring(3).toCharArray();
        field[0] = getterMethod.toLowerCase().charAt(3);
        return new String(field);
    }
}
