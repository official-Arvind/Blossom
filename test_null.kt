import org.schabi.newpipe.extractor.downloader.CancellableCall
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response

class NullDownloader : Downloader() {
    override fun execute(request: Request): Response = TODO()
    override fun executeAsync(request: Request, callback: Downloader.AsyncCallback?): CancellableCall? = null
}
