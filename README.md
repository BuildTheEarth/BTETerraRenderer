![Logo](core/src/main/resources/icon.png)
# BTETerraRenderer

![workflow](https://github.com/BuildTheEarth/BTETerraRenderer/actions/workflows/gradle.yml/badge.svg) [![Discord Chat](https://img.shields.io/discord/706317564904472627.svg)](https://discord.gg/4gjrwWH2gS)

A map hologram rendering tool for the BuildTheEarth project.<br>
Use this mod to easily map accurate road details and building tops.

Logo by [vicrobex](https://github.com/vicrobex)


## Supported maps

* Global
  * [OpenStreetMap](http://openstreetmap.org/)
  * [Bing maps](https://www.bing.com/maps/)
  * [Yandex.Maps](https://yandex.com/maps/)
  * [Google Earth](https://earth.google.com/web/)
* (And many others)

You can add other map services by adding/editing configuration files.<br>
Visit the [wiki page](https://github.com/BuildTheEarth/BTETerraRenderer/wiki) for more information.

## How to use

1. [Download](https://github.com/BuildTheEarth/BTETerraRenderer/releases) the latest version of the mod
2. Put the mod in the `mods` folder
   1. Open Minecraft Launcher and go to the `Installations` tab
   2. Find the installation profile, hover your mouse on it, and click the folder icon
   3. The `mods` folder is there
3. Run Minecraft

## Controls

| Key               | Description                                   |
|-------------------|-----------------------------------------------|
| `` ` ``(Backtick) | Opens render settings UI                      |
| `R`               | Toggles map rendering                         |
| `Y`               | Moves map up along the Y-axis by 0.5 blocks   |
| `I`               | Moves map down along the Y-axis by 0.5 blocks | 

## Screenshots

![Reference screenshot](docs/screenshot0.png "Location: Seattle, Washington State, USA")

![Reference screenshot](docs/screenshot1.png "Location: Manhattan, New York, USA")

Visit this [wiki page](https://github.com/BuildTheEarth/BTETerraRenderer/wiki/How-to-Add-Google-Earth) for more
information on how to add Google Earth as a map source.

## Development

### How to build

```bash
# Cloning the repository and its submodules:
git clone https://github.com/BuildTheEarth/BTETerraRenderer --recursive

# Building for all available MC versions:
./gradlew cleanModProjects
./gradlew copyBuildResultToRoot

# Building for specific MC versions:
# Cleaning is to refresh the core subproject dependency.
./gradlew :fabric1.21.11:clean
./gradlew :fabric1.21.11:build
```

After building for MC versions you can find the jar files in `build/libs` directory.

## Nightly builds

The latest nightly builds can be
found [here](https://github.com/BuildTheEarth/BTETerraRenderer/actions/workflows/gradle.yml). You can also download them
from there.

## Additional Info:

This is an updated version of the original mod, BTETerraRenderer, created by
tf2mandeokyi (https://github.com/tf2mandeokyi/BTETerraRenderer). We tried reaching out to the creator multiple times on
different platforms, but received no response. The focus is on fixing bugs, supporting new versions, and performing
other maintenance work.
Many thanks to @Amrsatrio for his significant contributions to the new version.
