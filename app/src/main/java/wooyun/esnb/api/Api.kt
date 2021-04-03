package wooyun.esnb.api

object Api {
    private val api1 = "https://api.ixiaowai.cn/api/api.php"
    private val api2 = "http://www.dmoe.cc/random.php"
    private val api3 = "https://www.rrll.cc/tuceng/ecy.php"

    fun getApi(): String {
        val listOf = listOf(api1, api2, api3)
        val randoms = (listOf.indices).random()
        return listOf[randoms]
    }

}