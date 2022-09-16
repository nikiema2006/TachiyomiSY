package exh.metadata.metadata

import android.content.Context
import eu.kanade.tachiyomi.source.R
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.copy
import exh.metadata.MetadataUtil
import exh.metadata.metadata.base.RaisedSearchMetadata
import exh.util.nullIfEmpty
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class HitomiSearchMetadata : RaisedSearchMetadata() {
    var url get() = hlId?.let { urlFromHlId(it) }
        set(a) {
            a?.let {
                hlId = hlIdFromUrl(a)
            }
        }

    var hlId: String? = null

    var title by titleDelegate(TITLE_TYPE_MAIN)

    var thumbnailUrl: String? = null

    var artists: List<String> = emptyList()

    var genre: String? = null

    var language: String? = null

    var uploadDate: Long? = null

    override fun createMangaInfo(manga: SManga): SManga {
        val cover = thumbnailUrl

        val title = title

        // Copy tags -> genres
        val genres = tagsToGenreString()

        val artist = artists.joinToString()

        val status = SManga.UNKNOWN

        val description = "meta"

        return manga.copy(
            thumbnail_url = cover ?: manga.thumbnail_url,
            title = title ?: manga.title,
            genre = genres,
            artist = artist,
            status = status,
            description = description,
        )
    }

    override fun getExtraInfoPairs(context: Context): List<Pair<String, String>> {
        return with(context) {
            listOfNotNull(
                getItem(hlId) { getString(R.string.id) },
                getItem(title) { getString(R.string.title) },
                getItem(thumbnailUrl) { getString(R.string.thumbnail_url) },
                getItem(artists.nullIfEmpty(), { it.joinToString() }) { getString(R.string.artist) },
                getItem(genre) { getString(R.string.genre) },
                getItem(language) { getString(R.string.language) },
                getItem(uploadDate, { MetadataUtil.EX_DATE_FORMAT.format(Date(it)) }) { getString(R.string.date_posted) },
            )
        }
    }

    companion object {
        private const val TITLE_TYPE_MAIN = 0

        const val TAG_TYPE_DEFAULT = 0

        const val BASE_URL = "https://hitomi.la"

        fun hlIdFromUrl(url: String) =
            url.split('/').last().split('-').last().substringBeforeLast('.')

        fun urlFromHlId(id: String) =
            "$BASE_URL/galleries/$id.html"
    }
}