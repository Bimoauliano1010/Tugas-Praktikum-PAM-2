package com.example.tugaspraktikumpam2.repository

import com.example.tugaspraktikumpam2.model.News
import com.example.tugaspraktikumpam2.model.NewsDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class NewsRepository {

    private val rawNewsList = listOf(
        News(1, "Peluncuran Satelit Terbaru Indonesia", "Teknologi", "Indonesia sukses meluncurkan satelit komunikasi baru untuk meningkatkan konektivitas digital di wilayah 3T.", "10:00 WIB"),
        News(2, "Timnas Indonesia Raih Kemenangan Dramatis", "Olahraga", "Timnas Indonesia berhasil menundukkan lawan dengan skor 2-1 pada menit-menit akhir pertandingan.", "10:02 WIB"),
        News(3, "ITERA Terapkan Kurikulum Berbasis AI", "Edukasi", "Institut Teknologi Sumatera mulai mengintegrasikan kecerdasan buatan ke dalam kurikulum pembelajaran.", "10:04 WIB"),
        News(4, "Perkembangan Processor Chip Terbaru 2026", "Teknologi", "Arsitektur chip 2nm siap diproduksi massal dengan efisiensi daya hingga 40 persen.", "10:06 WIB"),
        News(5, "Kompetisi Pemrograman Mahasiswa Nasional", "Edukasi", "Ratusan mahasiswa dari berbagai perguruan tinggi berlaga dalam kompetisi koding tingkat nasional.", "10:08 WIB"),
        News(6, "Atlit Bulutangkis Indonesia Juara All England", "Olahraga", "Ganda putra Indonesia kembali membawa pulang gelar juara All England setelah final sengit.", "10:10 WIB"),
        News(7, "Terobosan Mobil Listrik dengan Baterai Solid-State", "Teknologi", "Baterai generasi baru menawarkan jarak tempuh 1.000 km dengan pengisian daya hanya 10 menit.", "10:12 WIB"),
        News(8, "Beasiswa Riset Teknologi untuk Mahasiswa", "Edukasi", "Kementerian Pendidikan membuka pendaftaran beasiswa riset teknologi untuk mahasiswa jenjang S1 dan S2.", "10:14 WIB")
    )

    // StateFlow untuk menyimpan jumlah berita yang sudah dibaca (Requirement StateFlow)
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    private val readNewsIds = mutableSetOf<Int>()

    // Flow builder yang mensimulasikan data berita baru setiap 2 detik (Requirement Flow)
    fun getNewsStream(): Flow<News> = flow {
        var index = 0
        while (true) {
            val newsItem = rawNewsList[index % rawNewsList.size]
            val currentNews = newsItem.copy(
                id = index + 1,
                isRead = readNewsIds.contains(index + 1)
            )
            emit(currentNews)
            delay(2000) // Emit setiap 2 detik
            index++
        }
    }

    // Menggabungkan filter, map, dan onEach (Requirement Operators)
    fun getFilteredAndTransformedNewsStream(
        selectedCategory: String,
        onLogAction: (String) -> Unit
    ): Flow<NewsDisplay> {
        return getNewsStream()
            .filter { news ->
                // Operator filter: filter berdasarkan kategori
                selectedCategory == "Semua" || news.category.equals(selectedCategory, ignoreCase = true)
            }
            .map { news ->
                // Operator map: transformasi ke NewsDisplay
                NewsDisplay(
                    id = news.id,
                    formattedTitle = "[${news.category}] ${news.title}",
                    category = news.category,
                    content = news.content,
                    formattedTime = "Waktu: ${news.timestamp}",
                    isRead = readNewsIds.contains(news.id)
                )
            }
            .onEach { newsDisplay ->
                // Operator onEach: side effect saat berita diterima
                onLogAction("Diterima berita #${newsDisplay.id} (${newsDisplay.category})")
            }
    }

    // Menandai berita sebagai dibaca dan meng-update StateFlow
    fun markAsRead(newsId: Int) {
        if (!readNewsIds.contains(newsId)) {
            readNewsIds.add(newsId)
            _readCount.value = readNewsIds.size
        }
    }

    // Coroutine asynchronous fetch detail berita dengan async/await & Dispatchers (Requirement Coroutines)
    suspend fun fetchNewsDetailAsync(news: NewsDisplay): String = withContext(Dispatchers.Default) {
        val deferredDetail = async {
            delay(500) // Simulasi proses async
            "DETAIL BERITA TERVERIFIKASI\n\n" +
            "Judul: ${news.formattedTitle}\n" +
            "${news.formattedTime}\n" +
            "Kategori: ${news.category}\n\n" +
            "Isi Berita:\n${news.content}"
        }
        deferredDetail.await()
    }
}
