package wooyun.esnb.bean;

public class Ky {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Ky(String name, String info, String url) {
        this.name = name;
        this.info = info;
        this.url = url;
    }

    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String info;
    private String url;
}
