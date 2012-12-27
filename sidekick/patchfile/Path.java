// :indentSize=4:tabSize=4:noTabs=true:
package sidekick.patchfile;

import java.util.regex.Pattern;

/**
 * The Path class represents a path in a Patch/Diff file.
 * It provides simple queries and manipulation.
 *
 * Path seperators are assumed to be POSIX-style. Empty paths or "/dev null/"
 * will be ignored and are interpreted as indicating file creation / deletion.
 * Quoted paths with escaped characters will be simply stripped, retaining the
 * escaped form.
 */
class Path {
    private final static Pattern PATH_CLEANUP = Pattern.compile(
        "^/{3,}" +          // more than 2 leading "/"
        "|(?<=./)/+" +      // multiple "/" (not at beginning)
        "|(?<=/|^)\\./+" +  // "." directory
        "|/+\\.?/*$"        // trailing "/" and "."
        );

    String dir;
    String file;
    Path(String path) {
        if (path.length() == 0 || path.equals("/dev/null")) {
            this.dir = "";
            this.file = "";
            return;
        }

        if (path.startsWith("\"") && path.endsWith("\"")) {
            path = path.substring(1, path.length()-1);
        }

        final int lastPathSep = path.lastIndexOf('/');
        if (lastPathSep == -1) {
            this.dir = "";
            this.file = path;
        } else {
            this.dir = PATH_CLEANUP.matcher(path.substring(0, lastPathSep)
                                            ).replaceAll("");
            this.file = path.substring(lastPathSep+1);
        }
    }

    /**
     * Check if first path element (but not the filename) matches prefix.
     */
    boolean hasPrefix(final String prefix) {
        return this.dir.equals(prefix) || this.dir.startsWith(prefix+"/");
    }

    /**
     * Remove the first path element (but not the filename).
     */
    void removePrefix() {
        final int i = dir.indexOf('/');
        if (i == -1) {
            this.dir = "";
        } else {
            this.dir = dir.substring(i+1);
        }
    }

    /**
     * <code>true</code> if no filename was specified or it should be
     * ignored.
     */
    boolean isUnspecified() {
        return this.file.length() == 0;
    }

    /**
     * Return canonical form of this path.
     */
    public String toString() {
        if (dir.length() > 0) {
            return dir+"/"+file;
        } else {
            return file;
        }
    }
}
