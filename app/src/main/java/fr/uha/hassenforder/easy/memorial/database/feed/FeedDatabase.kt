package fr.uha.hassenforder.easy.memorial.database.feed

import android.util.JsonReader
import fr.uha.hassenforder.easy.android.repository.ManagementHelper
import fr.uha.hassenforder.easy.memorial.database.MemorialDatabase
import fr.uha.hassenforder.easy.memorial.model.*

class FeedDatabase (
    private val db : MemorialDatabase
) {

    private suspend fun build(tour : FullTour) {
        val id = db.getTourDao().create(tour.tour)
        tour.guides.forEach {
            db.getPoiDao().create(it.poi)
            db.getTriggerDao().create(it.trigger)
            val guide = it.guide.copy(
                tourId = tour.tour.tourId,
                poiId = it.poi.poiId,
                triggerId = it.trigger.triggerId
            )
            db.getGuideDao().create(guide)
        }
        val helper = ManagementHelper(db.getManagementDao())
        helper.create(id, Tour::class.simpleName!!)
        helper.valid(id, Tour::class.simpleName!!, true)
    }

    @Suppress("unused")
    suspend fun populate() {
        val tour = MulhouseTourBuilder().provideTour()
        if (tour != null) build (tour)
    }

    @Suppress("unused")
    suspend fun populate(input : JsonReader) {
        val tour = JsonTourBuilder(input).provideTour()
        if (tour != null) build (tour)
    }

    fun clear() {
        db.clearAllTables()
    }

}