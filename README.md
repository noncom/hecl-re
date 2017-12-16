Year 2018, Hecl?
================

When searching for a scripting language to use in my Java/Kotlin projects, I've spent much time looking at different possibilities, and came across Hecl, a language that was in development in around 2009.

The key features of the language seemed attractive and I thought it would worth a try. However, I could not use it since the project structure and the building cycle were too archaic. I had to make this port in order to be able to test out the langauge.

This is a personal project made for fun, an attempt at reviving of the original Hecl scripting language project by David N. Welton, http://www.hecl.org/


What happened?
==============

The following had been done:

* Moved the project to Gradle
* Removed all parts that were related to J2ME and other Java minorities that would be way too archaic in 2018 (see the **Multiproject Notice** below)
* Moved all the #ifdef compilation flags that were intended to be used by the original Ant build scripts to resolve in runtime from the new class `_Settings`
* Tried to save as much of the original code as possible, only cutting out the parts that were completely irrelevant
* All the original documentation and information texts are preserved (look inside the `info` folder)
* The artifact is published to Clojars Maven repo https://clojars.org/hecl and you can get it into your Mavenized projects, just be sure to add the Clojars repo to your repos.  

What else is there to do?
=========================

The work is not yet over, there are some things left:

* Possibly: relocate all the out-of-the-box commands to a separate package instead of them just littering the `org.hecl` package
* Possibly: break down the core package in subpackages
* Transform the project to a Kotlin project. For the powers.
* Publish the resulting artifact to other maven repos?
* Port the original Hecl Android part to a modern Android project that would depend on the main library artifact (see the **Multiproject Notice** below)

The package relocations would require fixing the javadocs which mentions their paths. IDEA can take care of some of it, but I did not yet try to actually do it and see how would it work.

The multiproject notice
=======================

There is a change to preserve the original versatility of Hecl that would facilitate the JavaME usage. Originally I did not bother but later I thought that it would be nice to salvage everything since looks like all the necessary deps are available with Maven.

However, I did not yet venture fully into this, but the ground is prepared. It's in the `multiproject` branch. The Hecl that is cleaned of the JavaME stuff (the same version that is in the `master` branch) is in the `core` subproject there. In order to port the JavaME support, just move the JavaSE versions of classes into the `SE` subproject and the JavaME versions into the `ME` subproject, leaving only the platform-independant classes in the `core`. You will need the original Hecl project where the JavaME part can be taken from. If you feel like it, go ahead, PRs are welcome.

As for the Android part of the original Hecl: Hecl in itself does not limit its usage on Android, you can use the `master` version just ok. However, there is some Android-API-ready code in the original Hecl that is not ported. Porting it would require a project that is dependant on the `core`-`SE` version of Hecl, and since it would be an Android Studio project, with all the requirements that usually come with it (like having the Android SDK installed), should be satisfied. That better be done as a separate project. Maybe I'll do it later, but PRs are, too, welcome. 

----------------------------------------------------------------

Below is the readme for the original Hecl project

----------------------------------------------------------------

Hecl Programming Language
=========================

The Hecl Programming Language is an attempt to create a programming
language that works the way I want it to.  The goals I have in mind
are:

* Simplicity.  The language shouldn't be complicated.  It should
  "scale down", which means that even someone without much experience
  programming ought to be able to use it and be productive with it,
  even if their code isn't beautiful.

* Power.  Experts should be able to use it and not feel limited.

* Small Core.  The core language should be simple and compact.
  Modules provide additional functionality.

* Extensible.  Hecl may be used to extend Hecl, as well as Java, of
  course.  The syntax is flexible enough that it is possible to create
  new control structures, for example, entirely in Hecl itself.

* Embeddable.  Hecl is easy to embed into your own Java applications.

* JavaME/J2ME.  Hecl is built to run in Java ME, which provides a much
  smaller API than 'regular' Java.

To get started, read the INSTALL file and the documentation, starting
with docs/index.html.

----

David N. Welton - davidw@dedasys.com
