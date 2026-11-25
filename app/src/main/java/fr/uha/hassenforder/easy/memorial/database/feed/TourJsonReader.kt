package fr.uha.hassenforder.easy.memorial.database.feed

import android.net.Uri
import android.util.JsonReader
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.Trigger
import fr.uha.hassenforder.easy.memorial.model.WithHelper
import java.io.IOException


data class JsonTourBuilder (private val input : JsonReader)
{
    private fun uriAssetBuilder (filename : String) : Uri {
        return Uri.Builder()
            .scheme("file")
            .encodedAuthority("/android_asset")
            .encodedPath(filename)
            .build()
    }

    @Throws(IOException::class)
    private fun readUri(input: JsonReader): Uri? {
        var type : String? = null
        var filename : String? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "type") type = input.nextString()
            if (name == "name") filename = input.nextString()
        }
        input.endObject()
        if (type == "asset" && filename != null) return uriAssetBuilder(filename)
        return null
    }

    @Throws(IOException::class)
    private fun readTour(input: JsonReader): Tour {
        var title : String? = null
        var description : String? = null
        var picture : Uri? = null
        var mode : TourMode? = null
        var withPicture : WithHelper? = null
        var withHint : WithHelper? = null
        var withDelay : WithHelper? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "title") title = input.nextString()
            if (name == "description") description = input.nextString()
            if (name == "picture") picture = readUri(input)
            if (name == "mode") mode = TourMode.valueOf(input.nextString())
            if (name == "withPicture") withPicture = WithHelper.valueOf(input.nextString())
            if (name == "withHint") withHint = WithHelper.valueOf(input.nextString())
            if (name == "withDelay") withDelay = WithHelper.valueOf(input.nextString())
        }
        input.endObject()
        return Tour(0,
            title ?: "",
            description ?: "",
            picture,
            mode ?: TourMode.USER,
            withPicture ?: WithHelper.USER,
            withHint ?: WithHelper.USER,
            withDelay ?: WithHelper.USER
        )
    }

    @Throws(IOException::class)
    private fun readPOI(input: JsonReader): POI {
        var poiname : String? = null
        var description : String? = null
        var picture : Uri? = null
        var media : Uri? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "name") poiname = input.nextString()
            if (name == "description") description = input.nextString()
            if (name == "picture") picture = readUri(input)
            if (name == "media") media = readUri(input)
        }
        input.endObject()
        return POI(0,
            poiname ?: "",
            description ?: "",
            picture,
            media,
        )
    }

    @Throws(IOException::class)
    private fun readTrigger(input: JsonReader): Trigger {
        var label : String? = null
        var latitude : String? = null
        var longitude : String? = null
        var hint : String? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "label") label = input.nextString()
            if (name == "latitude") latitude = input.nextString()
            if (name == "longitude") longitude = input.nextString()
            if (name == "hint") hint = input.nextString()
        }
        input.endObject()
        return Trigger(0,
            label ?: "",
            latitude ?: "",
            longitude ?: "",
            hint ?: "",
        )
    }

    @Throws(IOException::class)
    private fun readGuide(input: JsonReader) : Guide {
        var position : Int? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "position") position = input.nextInt()
        }
        input.endObject()
        return Guide(0, 0, 0, 0,
            position ?: 1,
        )
    }

    @Throws(IOException::class)
    private fun readFullGuide(input: JsonReader): FullGuide? {
        var guide : Guide? = null
        var poi : POI? = null
        var trigger : Trigger? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "guide") guide = readGuide(input)
            if (name == "poi") poi = readPOI(input)
            if (name == "trigger") trigger = readTrigger(input)
        }
        input.endObject()
        if (guide == null) return null
        if (poi == null) return null
        if (trigger == null) return null
        return FullGuide(
            guide,
            poi,
            trigger
        )
    }

    @Throws(IOException::class)
    private fun readFullGuideArray(input: JsonReader): List<FullGuide> {
        val guides: MutableList<FullGuide> = ArrayList()
        input.beginArray ()
        while (input.hasNext()) {
            val guide = readFullGuide(input)
            if (guide != null)
                guides.add(guide)
        }
        input.endArray ()
        return guides
    }

    @Throws(IOException::class)
    private fun readFullTour(input: JsonReader): FullTour? {
        var tour : Tour? = null
        var guides: List<FullGuide>? = null
        input.beginObject()
        while (input.hasNext()) {
            val name = input.nextName()
            if (name == "tour") tour = readTour(input)
            if (name == "guides") guides = readFullGuideArray(input)
        }
        input.endObject()
        if (tour == null) return null
        return FullTour(
            tour,
            guides?: emptyList()
        )
    }

    fun provideTour(): FullTour? {
        try {
            val tour = readFullTour(input)
            input.close()
            return tour
        }
        catch (_ :IOException) {
            input.close()
            return null
        }
    }

}