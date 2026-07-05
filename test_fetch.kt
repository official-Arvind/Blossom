import com.example.api.fetchRealMusic
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val res = fetchRealMusic("believer")
    println(res)
}
