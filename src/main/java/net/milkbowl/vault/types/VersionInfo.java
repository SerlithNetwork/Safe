package net.milkbowl.vault.types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionInfo {

    double currentVersion;
    String currentVersionTitle;
    double newVersion;
    String newVersionTitle;

    public VersionInfo(double currentVersion, String currentVersionTitle, double newVersion, String newVersionTitle) {
        this.currentVersion = currentVersion;
        this.currentVersionTitle = currentVersionTitle;
        this.newVersion = newVersion;
        this.newVersionTitle = newVersionTitle;
    }

}
