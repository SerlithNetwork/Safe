# Safe - Drop-in Vault replacement

# Ideas
1. Update API to keep-up with Paper's hard-fork
2. Remove bloat from chat/permissions plugins that nobody uses nowadays
3. Check if there's room for improvement
4. Migrate commands to Brigadier

> [!WARNING]
> Project under SLOW development

## For Developers:
Please see the [VaultUnlockedAPI](https://github.com/TheNewEconomy/VaultUnlockedAPI) page for
information on developing with Vault's API. In the past, you would use the same
artifact as servers installed, but the API has now been split from the main
project and is under a different artifact name. Please make sure you accommodate
this change in your build process.

## Installing
Installing Safe is as simple as copying the provided binary to your
`/plugins` directory, and the rest is automatic! If you
wish to perform configuration changes, this can be done via a configuration
file but should not be necessary in most cases. See the "Advanced
Configuration" section for more information.

## Why Safe?
No valid reasons honestly, if Paper doesn't break the Bukkit API that Vault uses, then Vault is more than enough. \
Safe aims to use modern API features, avoiding now `@Deprecated` API.

## Permissions
* vault.admin
  - Determines if a player should receive the update notices

## Building
Safe changed Vault's Maven to Gradle, to build the project just execute
```shell
./gradlew build
```

## Dependencies
No longer needs binaries to compile.

## Supported Plugins
Safe no longer contains built-in support for old, long-abandoned plugins.
Plugins like Luckperms have support for Vault although, Luckperms needs the plugin name to be exactly "Vault" (instead of "Safe") for some reason
