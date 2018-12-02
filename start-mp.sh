#!/bin/bash

usage() {
    printf "Usage: %s: [-h] [-m MPPASS] [-p PORT] [-s SERVER] [-u USERNAME]\n" $0
    printf "  -h		Show this help information\n"
    printf "  -m		Set the mppass (used for name verification)\n"
    printf "  -p		Set the server port to connect to\n"
    printf "  -s		Set the server hostname to connect to\n"
    printf "  -u		Set the username\n"
}

set -e
additional_properties=

while getopts "hm:p:s:u:" opt; do
    case $opt in
        h) usage; exit 2;;
        m) additional_properties+=" -Dminecraft.param.mppass=$OPTARG";;
        p) additional_properties+=" -Dminecraft.param.port=$OPTARG";;
        s) additional_properties+=" -Dminecraft.param.server=$OPTARG";;
        u) additional_properties+=" -Dminecraft.param.username=$OPTARG";;
        *) usage; exit 2;;
    esac
done

shift $((OPTIND-1))

launcher_pkg=

case $1 in
    0.0.13a_03)
        launcher_pkg="minecraft013a"
        ;;
    0.0.23a_01)
        launcher_pkg="minecraft023a"
        ;;
    0.28_01|0.29_02|0.30)
        launcher_pkg="minecraft030"
        ;;
    *)
        echo "Unknown version '$1'.  Supported versions are: 0.0.13a_03 0.0.23a_1 0.28_01 0.29_02 0.30"
        exit 1
        ;;
esac

if [ ! -f "minecraft/minecraft-$1.jar" ]; then
    echo "Missing jar minecraft/minecraft-$1.jar.  Did you import it?"
    exit 1
fi

echo "props=$additional_properties"

java -cp lib/jinput.jar:lib/lwjgl.jar:lib/lwjgl_util.jar:minecraft/minecraft-$1.jar:build/ \
     -Djava.library.path=native/linux \
     -Djava.util.Arrays.useLegacyMergeSort=true \
     -Dminecraft.param.sessionid \
     $additional_properties \
     net.calzoneman.$launcher_pkg.Launcher
