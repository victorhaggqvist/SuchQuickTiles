# Such Quick Tiles

Custom Quick Settings Tiles for Android 6.0 Marshmallow and CyanogenMod devices with CM Platform SDK 2.

## What about it?
What it does do and not do
- Create tile
    - Click/long click action (no long click on CM)
        - App launcher
        - Shortcut
    - Tile Icon
        - AOSP, currently only bundled resources
        - CM, AOSP + drawable support, ie. app icons, custom icons not implemented (yet)
- Delete tile
    - Via System UIs, in app delete not implemented
- Recreate tiles on after device reboot

### What could a tile possibly do?
Pretty much anything one could do with a regular app. A tile click just fires of a BroadcastReceiver where all the heavy lifting is done.

## Credits
- AOSP
- CyanogenMod
- [@kcoppock](https://github.com/kcoppock) for https://github.com/kcoppock/BroadcastTileSupport
- The guy in the app icon, Runner by Diego Naive, from The Noun Project, https://thenounproject.com/term/runner/168818/
- The "abc" icon, alphabetical by Austin Andrews, https://materialdesignicons.com/icon/alphabetical
- The hand icon, Hand by Michele Zamparo, https://thenounproject.com/term/hand/10491/

## License

    Such Quick Tiles - Custom QS Tiles for Android M and CM devices
    Copyright (C) 2015  Victor Häggqvist

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.