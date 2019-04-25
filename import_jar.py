from jawa.classloader import ClassLoader
from jawa.constants import UTF8
import os.path
import re
import shutil
import sys
import zipfile

version_re = re.compile(r'Minecraft (?P<version>[\d._a]+)')

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
        'com/mojang/minecraft/d', # 0.21a
        'com/mojang/minecraft/k', # 0.23a
        'com/mojang/minecraft/l'  # 0.28/29/30
    ]

    for class_ in try_classes:
        for const in loader.search_constant_pool(path=class_, type_=UTF8,
                f=match_version):
            return extract_version(const)

    raise Exception('Unable to detect version')

if __name__ == '__main__':
    loader = ClassLoader(sys.argv[1])
    version = detect_version(loader)
    print('Detected version {}'.format(version))
    shutil.copy(sys.argv[1],
            os.path.join('minecraft', 'minecraft-{}.jar'.format(version)))
