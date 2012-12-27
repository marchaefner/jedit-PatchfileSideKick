// :indentSize=4:tabSize=4:noTabs=true:
package sidekick.patchfile;

import javax.swing.Icon;

/**
 * A SideKick Asset that's not abstract.
 */
class PatchfileAsset extends sidekick.Asset {
    PatchfileAsset(final String name) {
        super(name);
    }

    public Icon getIcon() {
        return null;
    }

    public String getShortString() {
        return getName();
    }

    public String getLongString() {
        return getName();
    }
}

