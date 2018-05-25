package ziweiyang.toppine.com.oschinadome.bean;

import java.io.Serializable;

/**
 * 字段有id,name,portrait,relation,gender
 */
public class Author implements Serializable {   //作者
    protected long id;  //id号
    protected String name;  //名字
    protected String portrait;  //肖像
    protected int relation; //关系
    protected int gender;   //性别
    private Identity identity;  //身份

    public Author() {
        relation = 4;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", relation=" + relation +
                ", gender=" + gender +
                ", identity=" + identity +
                '}';
    }

    public static class Identity implements Serializable {
        public boolean officialMember;//官方会员
        public boolean softwareAuthor;//软件验证
    }
}
