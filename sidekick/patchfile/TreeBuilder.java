package sidekick.patchfile;

import javax.swing.tree.DefaultMutableTreeNode;

import org.gjt.sp.jedit.Buffer;

import sidekick.SideKickParsedData;
import sidekick.IAsset;

/**
 * Utility class for building a structure tree.
 * Formatting of node labels happens here.
 */
class TreeBuilder {
    final Buffer buffer;
    final boolean isUnifiedDiff;
    final SideKickParsedData parsedData;
    IAsset currentAsset;

    TreeBuilder(final Buffer buffer, final boolean isUnifiedDiff) {
        this.buffer = buffer;
        this.isUnifiedDiff = isUnifiedDiff;
        this.parsedData = new SideKickParsedData(buffer.getName());
    }

    void addNode(final Object data) {
        parsedData.root.add(new DefaultMutableTreeNode(data));
    }

    void startAsset(final int startPos,
                    final String orig, final String new_) {
        final Path origPath = new Path(orig);
        final Path newPath = new Path(new_);
        if (isUnifiedDiff) {
            removeGitPrefixes(origPath, newPath);
        }
        currentAsset = new PatchfileAsset(formatName(origPath, newPath));
        currentAsset.setStart(buffer.createPosition(startPos));
    }

    void completeAsset(final int endPos) {
        currentAsset.setEnd(buffer.createPosition(endPos));
        parsedData.root.add(new DefaultMutableTreeNode(currentAsset));
    }

    SideKickParsedData getData() {
        return this.parsedData;
    }

    /**
     * git-diff prefixes paths with "a/" and "b/" by default.
     * Simply remove the prefixes if it looks like that happend.
     */
    private static void
    removeGitPrefixes(final Path origPath, final Path newPath) {
        if (origPath.hasPrefix("a")) {
            if (newPath.isUnspecified()) {
                origPath.removePrefix();
            } else if (newPath.hasPrefix("b")) {
                origPath.removePrefix();
                newPath.removePrefix();
            }
        } else if (origPath.isUnspecified() && newPath.hasPrefix("b")) {
            newPath.removePrefix();
        }
    }

    /**
     * Build the node label for the structure tree.
     */
    private static String
    formatName(final Path origPath, final Path newPath) {
        // NOTE: leading space as workaround to avoid HTML display.
        final StringBuilder name = new StringBuilder(" ");

        if (origPath.isUnspecified()) {         // => file was created
            name.append("+ ").append(newPath);
        } else if (newPath.isUnspecified()) {   // => file was deleted
            name.append("- ").append(origPath);
        } else {
            name.append(origPath);
            if (!newPath.dir.equals(origPath.dir)) {
                name.append(" > ").append(newPath);
            } else if (!newPath.file.equals(origPath.file)) {
                name.append(" > ").append(newPath.file);
            }
        }
        return name.toString();
    }
}
