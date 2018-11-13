all: 0.0.13a 0.0.23a 0.30

builddir:
	mkdir -p build

mergesort: builddir
	javac -d build net/calzoneman/util/Collections.java

0.0.13a: builddir
	javac -cp lib/jinput.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:minecraft/minecraft-0.0.13a_03.jar \
	      -d build \
	      net/calzoneman/minecraft013a/LocalApplet.java \
	      net/calzoneman/minecraft013a/Launcher.java

0.0.23a: builddir
	javac -cp lib/jinput.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:minecraft/minecraft-0.0.23a_01.jar \
	      -d build \
	      net/calzoneman/minecraft023a/LocalApplet.java \
	      net/calzoneman/minecraft023a/Launcher.java

0.30: builddir
	javac -cp lib/jinput.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:minecraft/minecraft-0.30.jar \
	      -d build \
	      net/calzoneman/minecraft030/LocalApplet.java \
	      net/calzoneman/minecraft030/Launcher.java

clean:
	rm -rf build/

.PHONY: clean
