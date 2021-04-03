package wooyun.esnb.bean

import android.graphics.Bitmap

//data class 应用场景一般为adapter数据获取
data class About(var info: String, var img: Int)
data class FiguresAndSets(var bitmap: Bitmap?, var api: String)
data class Ky(var name: String, var info: String, var url: String)
data class Img(var imgurl:String)