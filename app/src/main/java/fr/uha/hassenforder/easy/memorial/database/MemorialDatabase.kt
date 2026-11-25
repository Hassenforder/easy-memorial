package fr.uha.hassenforder.easy.memorial.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uha.hassenforder.easy.android.database.DatabaseTypeConverters
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.android.database.dao.ManagementDao
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao
import fr.uha.hassenforder.easy.memorial.database.dao.GuideProgressDao
import fr.uha.hassenforder.easy.memorial.database.dao.PoiDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao
import fr.uha.hassenforder.easy.memorial.database.dao.TriggerDao
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.GuideProgress
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.model.Trigger

@TypeConverters(DatabaseTypeConverters::class)
@Database(
    entities = [
        Tour::class,
        Guide::class,
        POI::class,
        Trigger::class,
        TourProgress::class,
        GuideProgress::class,
        Management::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class MemorialDatabase : RoomDatabase() {

    abstract fun getManagementDao () : ManagementDao

    abstract fun getTourDao () : TourDao

    abstract fun getPoiDao () : PoiDao

    abstract fun getTriggerDao () : TriggerDao

    abstract fun getGuideDao () : GuideDao

    abstract fun getGuideProgressDao () : GuideProgressDao

    abstract fun getTourProgressDao () : TourProgressDao

    companion object {
        private lateinit var instance : MemorialDatabase

        @Synchronized
        fun create (context : Context) : MemorialDatabase {
            instance = Room.databaseBuilder(context, MemorialDatabase::class.java, "memorial.db").build()
            return instance
        }

        @Synchronized
        fun get () : MemorialDatabase {
            return instance
        }
    }

}
