package ziweiyang.toppine.com.oschinadome.bean;

import java.io.Serializable;

/**
 * 图片bean
 */

public class Image implements Serializable {
    private int id;
    private String path;    //路径
    private String thumbPath;   //翻阅路径(清晰度压缩过的)
    private boolean isSelect;   //是否选择过
    private String folderName;  //文件名
    private String name;    //
    private long date;  //时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Image) {
            return this.path.equals(((Image) o).getPath());
        }
        return false;
    }
}
