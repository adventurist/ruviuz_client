package stronglogic.ruviuz.content;

/**
 * Created by logicp on 12/25/16.
 * Custom class for easy access to file info
 */

public class RuvFileInfo {

    private String filePath;

    private String filename;

    private String mime;

    private int num;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return this.num;
    }
}
