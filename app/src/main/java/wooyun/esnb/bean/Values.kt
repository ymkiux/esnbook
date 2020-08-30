package wooyun.esnb.bean

class Values {
    var id: Int? = null
    var title: String? = null
    var content: String? = null
    var time: String? = null

    override fun toString(): String {
        return "Values{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                '}'
    }
}
