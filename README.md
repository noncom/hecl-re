Hecl, year 2018
===============

When searching for a scripting language to use in my Java/Kotlin projects, I've spent much time looking at different possibilities, and came across Hecl, a language that was in development in around 2009.

The key features of the language seemed attractive and I thought it would worth a try. However, I could not use it since the project structure and the building cycle were too archaic. I had to make this port in order to be able to test out the langauge.

This is a personal project made for fun, an attempt at reviving of the original Hecl scripting language project by David N. Welton, http://www.hecl.org/


What happened?
==============

The following had been done:

* Moved the project to Gradle
* Removed all parts that were related to J2ME and other Java minorities that would be way too archaic in 2018
* Moved all the #ifdef compilation flags that were intended to be used by the original Ant build scripts to resolve in runtime from the new class `_Settings`
* Tried to save as much of the original code as possible, only cutting out the parts that were completely irrelevant
* All the original documentation and information texts are preserved


What else is there to do?
=========================

The work is not yet over, there are some things left:

* Possibly: relocate all the out-of-the-box commands to a separate package
* Possibly: break down the core package in subpackages
* Transform the project to a Kotlin project
* Publish the resulting artifact ot a maven repo
* Port the original Hecl Android part to a modern Android project that would depend on the main library artifact

The package relocations would require fixing the javadocs which mentions their paths. IDEA can take care of some of it, but I did not yet try to actually do it and see how would it work.

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
