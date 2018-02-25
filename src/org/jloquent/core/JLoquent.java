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
package org.jloquent.core;

import java.sql.SQLException;
import org.jloquent.usage.Person;

/**
 *
 * @author derickfelix
 * @date Feb 24, 2018
 */
public class JLoquent {

    public static void main(String[] args) throws SQLException {
        Person p = new Person();
        p.setId(1);
        p.setName("Steve Wand");
        p.setAddress("Vespucci St");
        p.setZipcode("654421");
        p.setSex('m');
        
        Person person = Person.find(3, Person::new);
        
        System.out.println(person.getName());
        //p.delete();
        /*List<Person> persons = Person.all(Person::new);

        for (Person person : persons) {
            System.out.println(person.getId() + ", "
                    + person.getName() + ", "
                    + person.getAddress() + ", "
                    + person.getZipcode() + ", "
                    + person.getSex());
        }*/
        
    }
}
