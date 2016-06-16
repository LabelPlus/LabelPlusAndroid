package io.github.sgqy.labeler.function;

import com.immomo.tools.file.FileManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the core data class of this app
 */
public class WorkData implements Serializable {

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String defaultDirectory = "__no_working_directory";
    private static final String targetTextFileName = "Result.txt";
    private static final String defaultIOEncoding = "UTF-8";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public DocumentContent dc = new DocumentContent();
    private String workingDirectory = defaultDirectory;

    public void setWorkingDirectory(File path) {
        setWorkingDirectory(path.getAbsoluteFile());
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String path) {
        workingDirectory = path;
    }

    private String getTargetPath() {
        return workingDirectory + "/" + targetTextFileName;
    }

    /**
     * returns relative file name.
     */
    private List<File> getWorkingDirectoryFileList() {
        checkDirectory();
        File dir = new File(workingDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException();
        }
        File file[] = dir.listFiles();
        List<File> ret = new ArrayList<>();
        for (File f : file) {
            if (f.exists() && f.isFile()) {
                String bareName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/") + 1);
                if (!bareName.equals(targetTextFileName)) {
                    ret.add(new File(bareName));
                }
            }
        }
        return ret;
    }

    public File getFullPathByIndex(int index) {
        return new File(workingDirectory + "/" + dc.fileItems.get(index).fileName);
    }

    private void checkDirectory() {
        if (workingDirectory.equals(defaultDirectory)) {
            throw new IllegalArgumentException();
        }
    }

    public void loadData() {
        newData();

        List<String> raw = FileManager.readAsLines(getTargetPath(), defaultIOEncoding);
        parseString(raw);
    }

    public void newData() {
        List<File> list = getWorkingDirectoryFileList();

        for (File f : list) {
            dc.fileItems.add(
                    new DocumentContent().new ContentFile(f.getName())
            );
        }
    }

    public void saveData() {
        checkDirectory();

        String document = toString();
        FileManager.write(document, getTargetPath(), defaultIOEncoding);
    }

    private int findFileItem(String fileName) {
        for (DocumentContent.ContentFile f : dc.fileItems) {
            if (f.fileName.equals(fileName)) {
                return dc.fileItems.indexOf(f);
            }
        }
        return -1;
    }

    public void addItemToFileItemByIndex(int index, DocumentContent.ContentFile.ContentItem item) {
        if (index != -1) {
            dc.fileItems.get(index).items.add(item);
        }
    }

    public void addItemToFileItem(String fileName, DocumentContent.ContentFile.ContentItem item) {
        int idx = findFileItem(fileName);
        addItemToFileItemByIndex(idx, item);
    }

    private void parseString(List<String> raw) {
        Pattern patternFileName = Pattern.compile("^>+\\[(.*)\\]<+$");
        Pattern patternItemLine = Pattern.compile("^-+\\[(.*)\\]-+\\[([\\d\\.]*)\\,([\\d\\.]*)(\\,(\\d*))?\\]$");

        int contentStart = 0;
        for (contentStart = 0; contentStart < raw.size(); ++contentStart) {
            if (raw.get(contentStart).matches(patternFileName.pattern())) {
                break;
            }
        }

        String currentFileName = "";
        DocumentContent.ContentFile.ContentItem tempItem = new DocumentContent().new ContentFile("").new ContentItem();

        ParseState parseState = ParseState.TestFileName;
        for (int i = contentStart; i < raw.size(); /**/) {
            switch (parseState) {
                case TestFileName: {
                    Matcher matcher = patternFileName.matcher(raw.get(i));
                    if (matcher.matches()) {
                        String fileName = matcher.group(1);
                        int find = findFileItem(fileName);
                        if (find != -1) {
                            currentFileName = fileName;
                            parseState = ParseState.GotFileName;
                        }
                    }
                    ++i;
                    break;
                }
                case GotFileName: {
                    parseState = ParseState.TestLineSignature;
                    break;
                }
                case TestLineSignature: {
                    Matcher matcher = patternItemLine.matcher(raw.get(i));
                    if (matcher.matches()) {
                        tempItem.rX = Float.parseFloat(matcher.group(2));
                        tempItem.rY = Float.parseFloat(matcher.group(3));
                        parseState = ParseState.GotLineSignature;
                    }
                    ++i;
                    break;
                }
                case GotLineSignature: {
                    parseState = ParseState.TestLineContent;
                    break;
                }
                case TestLineContent: {
                    if (raw.get(i).matches(patternFileName.pattern())) {
                        addItemToFileItem(currentFileName, tempItem);
                        tempItem = new DocumentContent().new ContentFile("").new ContentItem();
                        parseState = ParseState.TestFileName;
                    } else if (raw.get(i).matches(patternItemLine.pattern())) {
                        addItemToFileItem(currentFileName, tempItem);
                        tempItem = new DocumentContent().new ContentFile("").new ContentItem();
                        parseState = ParseState.TestLineSignature;
                    } else {
                        tempItem.content += raw.get(i);
                        if (!raw.get(i).matches("^[ \\t]*$")) {
                            tempItem.content += "\n";
                        }
                        if (i == raw.size() - 1) {
                            addItemToFileItem(currentFileName, tempItem);
                            tempItem = new DocumentContent().new ContentFile("").new ContentItem();
                        }
                        ++i;
                    }
                    break;
                }
                case GotLineContent: {
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return (new DocumentFramework()).generateDocument();
    }

    private enum ParseState {
        TestFileName, GotFileName,
        TestLineSignature, GotLineSignature,
        TestLineContent, GotLineContent
    }

    public class DocumentContent implements Serializable {
        public ArrayList<ContentFile> fileItems = new ArrayList<>();

        @Override
        public String toString() {
            String ret = "";
            for (ContentFile sf : fileItems) {
                ret += sf.toString() + "\n";
            }
            return ret;
        }

        public class ContentFile implements Serializable {
            public String fileName = "__default_filename";
            public ArrayList<ContentItem> items = new ArrayList<>();
            public ContentFile(String fileName) {
                this.fileName = fileName;
            }

            @Override
            public String toString() {
                String ret = String.format(">>>>>>>>[%s]<<<<<<<<\n", fileName);
                for (int i = 0; i < items.size(); ++i) {
                    ret += String.format("----------------[%d]----------------%s\n", i + 1, items.get(i).toString());
                }
                return ret;
            }

            public class ContentItem implements Serializable {
                public float rX = 0;
                public float rY = 0;
                public int group = 1;
                public String content = "";

                public void reset() {
                    rX = 0;
                    rY = 0;
                    group = 1;
                    content = "";
                }

                @Override
                public String toString() {
                    return String.format("[%.3f,%.3f,%d]\n%s", rX, rY, group, content);
                }
            }
        }
    }

    private class DocumentFramework {
        private final String blockBoader = "-\n";

        private final String header = "1,0\n";
        private final String groups = "group0\ngroup1\n";
        private final String tail = "";
        private final String comment = "Default Comment\nYou can edit me\n\n\n";

        public String generateDocument() {
            return header + blockBoader + groups + blockBoader + comment + dc.toString() + tail;
        }
    }
}
