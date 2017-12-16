/* Copyright 2007 David N. Welton - DedaSys LLC - http://www.dedasys.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package org.hecl.java;

import org.hecl.core.*;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The <code>JavaCmd</code> class is utilized to implement Hecl
 * commands that can interact with Java classes.  The real work is
 * done in the Reflector class, which maps between Hecl and Java
 * types.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class JavaCmd implements ClassCommand, Command {
    private static Vector commands = null;

    private String cmdname = null;
    private Class thisclass = null;
    private Reflector classreflector = null;

    public JavaCmd(String clsname)
            throws HeclException {

        classreflector = new Reflector(clsname);
        try {
            thisclass = Class.forName(clsname);
        } catch (Exception e) {
            //Hecl.logStacktrace(e);
            throw new HeclException("Error trying to create " + clsname + " : " + e.toString());
        }
    }

    public Thing cmdCode(Interp interp, Thing[] argv) throws HeclException {
        try {
            String argv1 = argv[1].toString();

            /* These are for all the attributes like -text -height, etc... */
            if (argv1.equals("-new")) {
                MethodProps mp = new MethodProps();
                mp.setProps(argv, 1);

                Thing[] targs;
                Thing cargs = mp.getProp("-new");

                /* Instantiate a new one.  */
                String thingclass = cargs.getVal().thingclass();
                /* Be careful not to turn it into a list over and over again. */
                if (thingclass.equals("list") || thingclass.equals("string")) {
                    targs = ListThing.getArray(cargs);
                } else {
                    targs = new Thing[]{cargs};
                }
                mp.delProp("-new");

                /* Create a new instance. */
                Thing newthing = classreflector.instantiate(targs);
                mp.evalProps(interp, ObjectThing.get(newthing), classreflector);
                return newthing;
            } else if (argv1.equals("-field")) {
                /* Access a field. */
                return classreflector.getConstField(argv[2].toString());
            } else if (argv1.equals("-methods")) {
                return classreflector.methods();
            } else if (argv1.equals("-constructors")) {
                return classreflector.constructors();
            } else {
                /* Try calling a static method. */
                return classreflector.evaluate(null, argv1, argv);
            }
/* 	} catch (InvocationTargetException te) {
	throw new HeclException("Constructor error: " + te.getTargetException().toString());  */
        } catch (Exception e) {
//	    Hecl.logStacktrace(e);
            e.printStackTrace();
            throw new HeclException(argv[0].toString() + " " +
                    argv[1].toString() + " error " + e.toString());
        }
    }

    public Thing method(Interp interp, ClassCommandInfo context, Thing[] argv)
            throws HeclException {
        if (argv.length > 1) {
            Object target = ObjectThing.get(argv[0]);
            String subcmd = argv[1].toString().toLowerCase();
            if (subcmd.equals("-field")) {
                if (argv.length == 3) {
                    return classreflector.getField(target, argv[2].toString());
                } else if (argv.length == 4) {
                    classreflector.setField(target, argv[2].toString(), argv[3]);
                    return argv[3];
                } else {
                    throw HeclException.createWrongNumArgsException(argv, 2, "fieldname ?fieldvalue?");
                }
            } else {
                /* Invoke the method.  */
                return classreflector.evaluate(target, subcmd, argv);
            }
        }
        throw HeclException.createWrongNumArgsException(argv, 2, "Object method [arg...]");
    }

    public Class getCmdClass() {
        return thisclass;
    }

    public String getCmdName() {
        return cmdname;
    }

    public static void load(Interp ip, String cname, String cmd)
            throws HeclException {

        if (commands == null) {
            commands = new Vector();
        }
        JavaCmd newjavacmd = new JavaCmd(cname);
        if (cmd != null) {
            ip.addCommand(cmd, newjavacmd);
        }
        ip.addClassCmd(newjavacmd.getCmdClass(), newjavacmd);
        commands.add(newjavacmd);
    }

    public static void unload(Interp ip) {
        Enumeration e = commands.elements();
        while (e.hasMoreElements()) {
            JavaCmd c = (JavaCmd) e.nextElement();
            ip.removeCommand(c.getCmdName());
            ip.removeClassCmd(c.getCmdClass());
        }
    }
}
