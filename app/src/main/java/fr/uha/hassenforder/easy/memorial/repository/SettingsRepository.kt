package fr.uha.hassenforder.easy.memorial.repository

import android.content.Context
import android.content.res.AssetManager
import android.util.JsonReader
import fr.uha.hassenforder.easy.memorial.database.feed.FeedDatabase
import fr.uha.hassenforder.easy.memorial.database.MemorialDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader

class SettingsRepository(
    private val dispatcher: CoroutineDispatcher,
    private val db: MemorialDatabase,
    private val context: Context,
) {
    private fun findFiles (assetManager: AssetManager, pattern:String) : List<String> {
        val regexp = Regex(pattern)
        val list = assetManager.list("")?.filter { regexp.matches(it) }
        return list?: emptyList()
    }

    fun getAll(): Flow<List<String>> = flow {
        val list = findFiles(context.assets, "tour-.*.json")
        emit(list)
    }

    suspend fun populate () = withContext(dispatcher) {
        FeedDatabase(db).populate()
    }

    suspend fun populate (filename : String) = withContext(dispatcher) {
        try {
            val inputStream = context.assets.open(filename)
            val reader = JsonReader(InputStreamReader(inputStream))
            FeedDatabase(db).populate(reader)
            reader.close()
            inputStream.close()
        } catch (_ : IOException) {
        }
    }

    suspend fun clear () = withContext(dispatcher) {
        FeedDatabase(db).clear()
    }

}
