package eu.kanade.tachiyomi.network.services

import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.network.awaitSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Serializable
data class AniListRecommendation(
    val title: String,
    val coverImage: String
) : java.io.Serializable

class AniListRecommendations(
    private val networkHelper: NetworkHelper = Injekt.get(),
    private val json: Json = Injekt.get()
) {

    @Serializable
    private data class AniListResponse(val data: DataResponse)
    
    @Serializable
    private data class DataResponse(val Media: MediaResponse? = null)
    
    @Serializable
    private data class MediaResponse(val recommendations: RecommendationsResponse? = null)
    
    @Serializable
    private data class RecommendationsResponse(val edges: List<EdgeResponse> = emptyList())
    
    @Serializable
    private data class EdgeResponse(val node: NodeResponse)
    
    @Serializable
    private data class NodeResponse(val mediaRecommendation: MediaRecommendationResponse? = null)
    
    @Serializable
    private data class MediaRecommendationResponse(
        val title: TitleResponse,
        val coverImage: CoverImageResponse
    )
    
    @Serializable
    private data class TitleResponse(
        val english: String? = null,
        val romaji: String? = null,
        val userPreferred: String? = null
    )
    
    @Serializable
    private data class CoverImageResponse(val large: String)

    suspend fun getRecommendations(title: String, type: String): List<AniListRecommendation> {
        val query = """
        query Recommendations(${'$'}query: String, ${'$'}type: MediaType) {
            Media(search: ${'$'}query, type: ${'$'}type) {
                recommendations(sort: RATING_DESC) {
                    edges {
                        node {
                            mediaRecommendation {
                                title {
                                    english
                                    romaji
                                    userPreferred
                                }
                                coverImage {
                                    large
                                }
                            }
                        }
                    }
                }
            }
        }
        """.trimIndent()

        val payload = buildJsonObject {
            put("query", query)
            putJsonObject("variables") {
                put("query", title)
                put("type", type) // "MANGA" or "ANIME"
            }
        }

        val request = POST(
            url = "https://graphql.anilist.co/",
            body = payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        )

        return try {
            val responseBody = networkHelper.client.newCall(request).awaitSuccess().body?.string() ?: return emptyList()
            val parsed = json.decodeFromString<AniListResponse>(responseBody)
            
            parsed.data.Media?.recommendations?.edges?.mapNotNull { edge ->
                val rec = edge.node.mediaRecommendation
                if (rec != null) {
                    AniListRecommendation(
                        title = rec.title.english ?: rec.title.romaji ?: rec.title.userPreferred ?: "Unknown Title",
                        coverImage = rec.coverImage.large
                    )
                } else null
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
