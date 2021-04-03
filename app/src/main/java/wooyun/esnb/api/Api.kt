package wooyun.esnb.api

import wooyun.esnb.bean.Img

object Api {
    private val api1 = "https://api.ixiaowai.cn/api/api.php"
    private val api2 = "http://www.dmoe.cc/random.php"
    private val api3 = "https://www.rrll.cc/tuceng/ecy.php"

    private var list: MutableList<Img> = ArrayList<Img>()

    fun getApi(): String {
        var img = Img(api1)
        list.add(img)
        img = Img(api2)
        list.add(img)
        img = Img(api3)
        list.add(img)
        val randoms = (0 until list.size).random()
        return list[randoms].imgurl
    }

}