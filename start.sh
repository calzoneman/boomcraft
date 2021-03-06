#!/bin/bash

usage() {
    printf "Usage: %s: [-ah] [-l LEVEL]\n" $0
    printf "  -a		Set admin flag (makes bedrock breakable)\n"
    printf "  -h		Show this help information\n"
    printf "  -l		Load level from a dat file\n"
}

set -e
additional_properties=

while getopts "ahl:" opt; do
    case $opt in
        a) additional_properties+=" -Dminecraft.admin=1";;
        h) usage; exit 2;;
        l)
           additional_properties+=" -Dminecraft.loadlevel=1"
           if [ ! -f "$OPTARG" ]; then
               echo "Cannot load level: '$OPTARG' doesn't exist or is not a file"
               exit 1
           fi

           cp "$OPTARG" level.dat
           ;;
        *) usage; exit 2;;
    esac
done

shift $((OPTIND-1))

launcher_pkg=

case $1 in
    0.0.13a_03|0.0.21a)
        launcher_pkg="minecraft013a"
        ;;
    0.0.23a_01)
        launcher_pkg="minecraft023a"
        ;;
    0.28_01|0.29_02|survivaltest|0.30)
        launcher_pkg="minecraft030"
        ;;
    *)
        echo "Unknown version '$1'.  Supported versions are: 0.0.13a_03 0.0.23a_1 0.28_01 0.29_02 survivaltest 0.30"
        exit 1
        ;;
esac

if [ ! -f "minecraft/minecraft-$1.jar" ]; then
    echo "Missing jar minecraft/minecraft-$1.jar.  Did you import it?"
    exit 1
fi

java -cp lib/jinput.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:minecraft/minecraft-$1.jar:build/ \
     -Djava.library.path=native/linux \
     -Djava.util.Arrays.useLegacyMergeSort=true \
     $additional_properties \
     net.calzoneman.$launcher_pkg.Launcher
