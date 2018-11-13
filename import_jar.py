from collections import namedtuple
from jawa.classloader import ClassLoader
from jawa.constants import UTF8
from jawa.constants import MethodReference
import os.path
import re
import sys
import zipfile

version_re = re.compile(r'Minecraft (?P<version>[\d._a]+)')
MinecraftVersion = namedtuple('MinecraftVersion', ['version', 'main_class'])

def detect_version(loader):
    def match_version(const):
        return 'Minecraft 0' in const.value

    def extract_version(const):
        match = version_re.match(const.value)
        if not match:
            raise Exception('Invalid version constant "{}"'.format(const))

        return match.group('version')

    try_classes = [
        'com/mojang/minecraft/c', # 0.13a
        'com/mojang/minecraft/k', # 0.23a
        'com/mojang/minecraft/l'  # 0.28/29/30
    ]

    for class_ in try_classes:
        for const in loader.search_constant_pool(path=class_, type_=UTF8,
                f=match_version):
            return MinecraftVersion(extract_version(const), class_)

    raise Exception('No suitable version found')

def patch_sort(loader, version):
    target_class = loader.load(version.main_class)

    def match_reference(const):
        return (const.class_.name.value == 'java/util/Collections'
                and const.name_and_type.name.value == 'sort'
                and const.name_and_type.descriptor.value
                    == '(Ljava/util/List;Ljava/util/Comparator;)V')

    for const in target_class.constants.find(type_=MethodReference,
            f=match_reference):
        print('Patching constant {}'.format(const))
        const.class_.name.value = 'net/calzoneman/util/Collections'

    return target_class

def copy_file(inz, outz, filename):
    with inz.open(filename, mode='r') as r:
        with outz.open(filename, mode='w') as w:
            buf = r.read(1024)
            while len(buf) > 0:
                w.write(buf)
                buf = r.read(1024)

if __name__ == '__main__':
    loader = ClassLoader(sys.argv[1])
    version = detect_version(loader)
    print('Detected version {}'.format(version))
    patched_class = patch_sort(loader, version)

    with zipfile.ZipFile(sys.argv[1], mode='r') as inz:
        with zipfile.ZipFile(
                os.path.join('minecraft', 'minecraft-{}.jar'.format(
                    version.version)),
                mode='w') as outz:
            for filename in inz.namelist():
                if filename == version.main_class + '.class':
                    print('Writing patched {}'.format(filename))
                    with outz.open(filename, mode='w') as dest:
                        patched_class.save(dest)
                elif not filename.startswith('META-INF'):
                    print('Copying {}'.format(filename))
                    copy_file(inz, outz, filename)
