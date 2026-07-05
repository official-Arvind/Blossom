import com.example.api.YTMusicApi
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val url = "https://www.youtube.com/watch?v=7wtfhZwyrcc" // Believer
    val res = YTMusicApi.getStreamUrl(url)
    println("Stream: " + res)
}
