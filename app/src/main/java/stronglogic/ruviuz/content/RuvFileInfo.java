package stronglogic.ruviuz.content;

/**
 * Created by logicp on 12/25/16.
 * Custom class for easy access to file info
 */

public class RuvFileInfo {

    private String filePath;

    private String filename;

    private String fileUrl;

//    private String mime;

    private String comment;

    private int ruvFid;


    private int num;

    public RuvFileInfo() {
        this.comment = "";
    }

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

    public void setUrl(String url) { this.fileUrl = url; }

    public String getUrl() { return this.fileUrl; }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return this.num;
    }

    public void setFid(int fid) { this.ruvFid = fid; }

    public int getFid() { return this.ruvFid; }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }
}

