/* Copyright 2006-2007 David N. Welton, DedaSys LLC

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

package org.hecl.jarhack;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.jar.*;

/**
 * <code>JarHack</code> -- this class provides several static methods
 * that can be used to create .jar and .jad files from a template
 * Hecl.jar and some user supplied information, such as the output
 * file, and the name of the new application.
 *
 * @author <a href="mailto:davidw@dedasys.com">David N. Welton</a>
 * @version 1.0
 */
public class JarHack {

    private static String cldcversion = "";
    private static String midpversion = "";

    /**
     * The <code>substHecl</code> method takes the filenames of two
     * .jar's - one as input, the second as output, in addition to the
     * name of the application.  Where it counts, the old name (Hecl,
     * usually) is overridden with the new name, and the new .jar file
     * is written to the specified outfile.  Via the iconname argument
     * it is also possible to specify a new icon file to use.
     *
     * @param infile   a <code>FileInputStream</code> value
     * @param outfile  a <code>String</code> value
     * @param newname  a <code>String</code> value
     * @param iconname a <code>String</code> value
     * @throws IOException if an error occurs
     */
    public static void substHecl(InputStream infile, String outfile,
                                 String newname, String iconname, String scriptfile)
            throws IOException {

        JarInputStream jif = new JarInputStream(infile);
        Manifest mf = jif.getManifest();
        Attributes attrs = mf.getMainAttributes();

        Set keys = attrs.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Object value = attrs.get(key);
            String keyname = key.toString();

            /* These are the three cases that interest us in
             * particular, where we need to make changes. */
            if (keyname.equals("MIDlet-Name")) {
                attrs.putValue(keyname, newname);
            } else if (keyname.equals("MIDlet-1")) {
                String valuestr = value.toString();
                /* FIXME - the stringsplit method is used for older
                 * versions of GCJ.  Once newer versions are common,
                 * it can go away.  Or not - it works just fine. */
                String properties[] = stringsplit(valuestr, ", ");
                attrs.putValue(keyname, newname + ", " + properties[1] + ", " + properties[2]);
            } else if (keyname.equals("MicroEdition-Configuration")) {
                cldcversion = value.toString();
            } else if (keyname.equals("MicroEdition-Profile")) {
                midpversion = value.toString();
            } else if (keyname.equals("MIDlet-Jar-URL")) {
                attrs.put(key, newname + ".jar");
            }
        }

        JarOutputStream jof = new JarOutputStream(new FileOutputStream(outfile), mf);

        byte[] buf = new byte[4096];

        /* Go through the various entries. */
        JarEntry entry;
        int read;
        while ((entry = jif.getNextJarEntry()) != null) {

            /* Don't copy the manifest file. */
            if ("META-INF/MANIFEST.MF".equals(entry.getName())) continue;

            /* Insert our own icon */
            if (iconname != null && "Hecl.png".equals(entry.getName())) {
                jof.putNextEntry(new JarEntry("Hecl.png"));
                FileInputStream inf = new FileInputStream(iconname);
                while ((read = inf.read(buf)) != -1) {
                    jof.write(buf, 0, read);
                }
                inf.close();
            }
            /* Insert our own copy of the script file. */
            else if ("script.hcl".equals(entry.getName())) {
                jof.putNextEntry(new JarEntry("script.hcl"));
                FileInputStream inf = new FileInputStream(scriptfile);
                while ((read = inf.read(buf)) != -1) {
                    jof.write(buf, 0, read);
                }
                inf.close();
            } else {
                /* Otherwise, just copy the entry. */
                jof.putNextEntry(entry);
                while ((read = jif.read(buf)) != -1) {
                    jof.write(buf, 0, read);
                }
            }

            jof.closeEntry();
        }

        jof.flush();
        jof.close();
        jif.close();
    }

    /**
     * The <code>createJadForJar</code> method calls createJadForJar
     * with a url created from the application's name.
     *
     * @param jarfile a <code>String</code> value
     * @param appname a <code>String</code> value
     * @throws IOException if an error occurs
     */
    public static void createJadForJar(String jarfile, String appname) throws IOException {
        String url = appname + ".jar";
        createJadForJar(jarfile, appname, url);
    }

    /**
     * The <code>createJadForJar</code> method creates a new .jad file
     * that matches the .jar file passed to it.  appname is the name
     * of the new application.
     *
     * @param jarfile a <code>String</code> value
     * @param appname a <code>String</code> value
     * @param url     a <code>String</code> value
     * @throws IOException if an error occurs
     */
    public static void createJadForJar(String jarfile, String appname, String url) throws IOException {
        File jf = new File(jarfile);
        String parent = jf.getParent();
        File jadfile = new File(parent + File.separatorChar + appname + ".jad");
        FileWriter of = new FileWriter(jadfile);
        of.write("MIDlet-1: " + appname + ", /Hecl.png, Hecl" + "\n" +
                "MIDlet-Info-URL: http://www.hecl.org" + "\n" +
                "MIDlet-Jar-Size: " + jf.length() + "\n" +
                "MIDlet-Jar-URL: " + url + "\n" +
                "MIDlet-Name: " + appname + "\n" +
                "MIDlet-Vendor: dedasys.com" + "\n" +
                "MIDlet-Version: 1.1" + "\n" +
                "MicroEdition-Profile: " + midpversion + "\n" +
                "MicroEdition-Configuration: " + cldcversion);
        of.close();
    }


    /**
     * <code>main</code> implements a command line version of JarHack
     * that takes five arguments: 1. The original 'template' .jar
     * file to use.  2. The destination directory where you want to
     * create your new application.  3. The name of your new
     * application as it will appear in the MIDlet.  4. The name of
     * the Hecl script you want to use instead of the script.hcl that
     * is included in the 'source' .jar file.  5. The URL where your
     * jar file will reside.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args) throws ParseException {
        Options opts = new Options();

        String sourcefile = null;
        String destdir = null;
        String appname = null;
        String scriptfile = null;
        String iconname = null;

        opts.addOption("hecljar", true, "Location of Java ME Hecl jar");
        opts.addOption("destdir", true, "Directory where the .jar and .jad will be created");
        opts.addOption("name", true, "Name of MIDLet to create: $name.jar and $name.jad");
        opts.addOption("script", true, "Script filename");
        opts.addOption("icon", true, "Script icon");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(opts, args);

        if (cmd.hasOption("hecljar")) {
            sourcefile = cmd.getOptionValue("hecljar");
        } else {
            usage(opts);
        }
        if (cmd.hasOption("destdir")) {
            destdir = cmd.getOptionValue("destdir");
        } else {
            usage(opts);
        }
        if (cmd.hasOption("name")) {
            appname = cmd.getOptionValue("name");
        } else {
            usage(opts);
        }
        if (cmd.hasOption("script")) {
            scriptfile = cmd.getOptionValue("script");
        } else {
            usage(opts);
        }
        if (cmd.hasOption("icon")) {
            iconname = cmd.getOptionValue("icon");
        }


        String url;
        if (args.length == 5) {
            url = args[4];
        } else {
            url = appname + ".jar";
        }

        if (!(new File(destdir)).isDirectory()) {
            System.err.println("Not a valid directory: " + destdir);
            System.exit(1);
        }

        if (iconname != null) {
            if (!(new File(iconname)).isFile()) {
                System.err.println("Not a valid icon: " + iconname);
                System.exit(1);
            }
        }

        String newfn = destdir + appname + ".jar";

        try {
            FileInputStream infile = new FileInputStream(sourcefile);
            substHecl(infile, newfn, appname, iconname, scriptfile);
            createJadForJar(newfn, appname, url);
        } catch (Exception e) {
            System.err.println("Error: " + e);
            e.printStackTrace();
        }
    }

    private static void usage(Options opts) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JarHack", opts);
        System.exit(1);
    }

    /**
     * The <code>stringsplit</code> method is here because older
     * versions of GCJ don't seem to handle String.split very well.
     *
     * @param in    a <code>String</code> value
     * @param split a <code>String</code> value
     * @return a <code>String[]</code> value
     */
    private static String[] stringsplit(String in, String split) {
        Vector ret = new Vector();
        int fromindex = 0;
        int idx = 0;
        int splitlen = split.length();
        while (true) {
            idx = in.indexOf(split, fromindex);
            if (idx < 0) break;
            ret.addElement(in.substring(fromindex, idx));
            fromindex = idx + splitlen;
        }
        ret.addElement(in.substring(fromindex));
        String[] retstr = new String[ret.size()];
        ret.copyInto(retstr);
        return retstr;
    }
}
