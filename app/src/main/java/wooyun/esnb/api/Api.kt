package wooyun.esnb.api

object Api {
    private val api_one = "https://api.ixiaowai.cn/api/api.php"
    private val api_two = "http://www.dmoe.cc/random.php"
    private val api_three = "https://www.rrll.cc/tuceng/ecy.php"
    private var list: MutableList<String> = ArrayList<String>()

    fun getApi(): String {
        list.add(api_one)
        list.add(api_two)
        list.add(api_three)
        val randoms = (0..list.size - 1).random()
        return list[randoms]
    }
}