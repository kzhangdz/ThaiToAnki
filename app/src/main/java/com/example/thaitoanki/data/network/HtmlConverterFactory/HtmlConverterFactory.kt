package com.example.thaitoanki.data.network.HtmlConverterFactory

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type


class HtmlConverterFactory(private val baseUrl: String): Converter.Factory() {

//    public fun create(url: String): HtmlConverterFactory {
//        return HtmlConverterFactory(url)
//    }

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation?>?, retrofit: Retrofit?
    ): Converter<ResponseBody, Document> {
        return HtmlResponseBodyConverter(baseUrl)
    }
}

class HtmlResponseBodyConverter(private val baseUrl: String) : Converter<ResponseBody, Document> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): Document {
        try {
            return Jsoup.parse(value.byteStream(), "UTF-8", baseUrl)
        } finally {
            value.close()
        }
    }
}