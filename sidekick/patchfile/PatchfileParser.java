// :indentSize=4:tabSize=4:noTabs=true:
package sidekick.patchfile;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.gjt.sp.jedit.Buffer;

import sidekick.SideKickParser;
import sidekick.SideKickParsedData;
import errorlist.DefaultErrorSource;

/**
 * SideKick parser service
 */
public class PatchfileParser extends SideKickParser {
    private final static String LINE_SEP = "(?:\r?\n|\r)";
    private final static Pattern UDIFF_HEADER = Pattern.compile(
        "^---\u0020([^\r\n\t]+).*" + LINE_SEP +
        "\\+{3}\u0020([^\r\n\t]+)",
        Pattern.MULTILINE);
    private final static Pattern CDIFF_HEADER = Pattern.compile(
        "^\\*{3}\u0020([^\r\n\t]+).*" + LINE_SEP +
        "---\u0020([^\r\n\t]+).*" + LINE_SEP +
        "[*]{15}",
        Pattern.MULTILINE);

    public
    PatchfileParser() {
        super("patchfile");
    }

    public SideKickParsedData
    parse(final Buffer buffer, final DefaultErrorSource errorSource) {
        final String text = buffer.getText();
        Matcher matcher = UDIFF_HEADER.matcher(text);
        final boolean isUnifiedDiff = matcher.find();
        final TreeBuilder tree = new TreeBuilder(buffer, isUnifiedDiff);
        if (!isUnifiedDiff) {
            matcher = CDIFF_HEADER.matcher(text);
            if (!matcher.find()) {
                tree.addNode("Not recognized.");
                return tree.getData();
            }
        }
        tree.startAsset(matcher.start(), matcher.group(1), matcher.group(2));
        while (matcher.find()) {
            final int start = matcher.start();
            tree.completeAsset(start);
            tree.startAsset(matcher.start(),
                            matcher.group(1), matcher.group(2));
        }
        tree.completeAsset(buffer.getLength());
        return tree.getData();
    }
}