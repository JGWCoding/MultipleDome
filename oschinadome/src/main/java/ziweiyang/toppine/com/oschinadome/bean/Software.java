package ziweiyang.toppine.com.oschinadome.bean;

/**
 * 字段有id,name,href
 */
public class Software extends PrimaryBean {
    private String name;
    private String href;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
