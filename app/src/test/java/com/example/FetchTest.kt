package com.example

import org.schabi.newpipe.extractor.ServiceList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FetchTest {
    @Test
    fun testFetch() = runBlocking {
        println(ServiceList.YouTube.serviceInfo.name)
        try {
            val ex = ServiceList.YouTube.getSearchExtractor("top hits")
            ex.fetchPage()
            println("Found YouTube items: " + ex.initialPage.items.size)
        } catch(e:Exception) { e.printStackTrace() }
        
        try {
            val ex = ServiceList.YouTubeMusic.getSearchExtractor("top hits")
            ex.fetchPage()
            println("Found YouTubeMusic items: " + ex.initialPage.items.size)
        } catch(e:Exception) { e.printStackTrace() }
    }
}
