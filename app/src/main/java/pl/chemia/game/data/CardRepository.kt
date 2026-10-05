package pl.chemia.game.data

import android.content.Context
import org.json.JSONArray
import pl.chemia.game.R
import pl.chemia.game.model.Category
import pl.chemia.game.model.GameCard
import pl.chemia.game.model.Intensity

class CardRepository(private val context: Context) {

    fun load(): List<GameCard> =
        parseRaw(R.raw.cards_core_pl) + parseRaw(R.raw.cards_mode_pl)

    private fun parseRaw(resId: Int): List<GameCard> {
        val json = context.resources.openRawResource(resId)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val array = JSONArray(json)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val intensity = Intensity.fromRank(item.getInt("intensity"))
                val tagsArray = item.optJSONArray("tags")
                val tags = buildSet {
                    if (tagsArray != null) {
                        for (tagIndex in 0 until tagsArray.length()) {
                            add(tagsArray.getString(tagIndex))
                        }
                    }
                }

                add(
                    GameCard(
                        id = item.getString("id"),
                        category = Category.valueOf(item.getString("category")),
                        intensity = intensity,
                        title = item.getString("title"),
                        text = item.getString("text"),
                        heat = item.optInt("heat", 0),
                        tags = tags,
                        chain = item.optBoolean("chain", false),
                        afterglow = item.optBoolean("afterglow", false),
                        requiresMutualYes = item.optBoolean(
                            "requiresMutualYes",
                            intensity.rank >= Intensity.HOT.rank,
                        ),
                    )
                )
            }
        }
    }
}
