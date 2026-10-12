# BehindYou

[![Modrinth downloads](https://img.shields.io/modrinth/dt/sAFPANmp?style=flat&logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/behindyou)
[![CurseForge downloads](https://img.shields.io/curseforge/dt/1147770?style=flat&logo=curseforge&label=CurseForge&color=f16436)](https://www.curseforge.com/minecraft/mc-mods/behindyou)

BehindYou adds two keys that switch your camera to third person. One key shows the view from behind you and the other shows your face. Other clients call this SnapLook.

BehindYou is off after you install it. Turn on "Enable BehindYou" in OneConfig, then bind the two keys. Neither key has a default binding.

## Features

- Set each key to hold or toggle. On hold, you go back to first person when you release the key. On toggle, you stay in the view until you press the key again. Both keys start on hold.
- The camera slides between views over 0.4 seconds. You can set the time between 0.1 and 2 seconds, or turn the animation off.
- For each view, choose when the animation plays: when you enter the view, when you return to first person, both or never.
- Set the FOV for each view between 30 and 110. Both views start at 90, whatever your normal FOV is. You can turn the FOV change off.
- Set how far the camera sits from you in each view, between 1 and 4 blocks. 4 blocks is the vanilla distance.
- An option applies the same animation, FOV and distance when you press Minecraft's own perspective key. It is off by default.

## Supported versions

- Minecraft 1.8.9 on Ornithe.
- Minecraft 1.21.1, 1.21.4, 1.21.5, 1.21.8, 1.21.10, 1.21.11, 26.1, 26.2 and 26.3 on Fabric.

BehindYou needs [OneConfig](https://modrinth.com/mod/oneconfig) and Fabric Language Kotlin.

## Support

- Report bugs and ask questions on the [Polyfrost Discord](https://polyfrost.org/discord).
- Check [Polyfrost Status](https://status.polyfrost.org/) if a Polyfrost service is down.

## Building

```sh
./gradlew ":26.3:build"
```

Replace `26.3` with any supported Minecraft version. The build needs JDK 21 and JDK 25 installed.
