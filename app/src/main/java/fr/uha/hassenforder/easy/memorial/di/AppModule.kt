package fr.uha.hassenforder.easy.memorial.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.android.database.dao.ManagementDao
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.navigation.NavigatorType
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao
import fr.uha.hassenforder.easy.memorial.database.dao.GuideProgressDao
import fr.uha.hassenforder.easy.memorial.database.MemorialDatabase
import fr.uha.hassenforder.easy.memorial.database.dao.PoiDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao
import fr.uha.hassenforder.easy.memorial.database.dao.TriggerDao
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.navigation.GreetingDestination
import fr.uha.hassenforder.easy.memorial.navigation.rootNavGraph
import fr.uha.hassenforder.easy.memorial.repository.GuideRepository
import fr.uha.hassenforder.easy.memorial.repository.SettingsRepository
import fr.uha.hassenforder.easy.memorial.repository.TourRepository
import fr.uha.hassenforder.easy.memorial.repository.PerformingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())

    @Singleton
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = MemorialDatabase.create(appContext)

    @Singleton
    @Provides
    fun provideManagementDao(db: MemorialDatabase) = db.getManagementDao()

    @Singleton
    @Provides
    fun provideTourDao(db: MemorialDatabase) = db.getTourDao()

    @Singleton
    @Provides
    fun provideGuideDao(db: MemorialDatabase) = db.getGuideDao()

    @Singleton
    @Provides
    fun providePoiDao(db: MemorialDatabase) = db.getPoiDao()

    @Singleton
    @Provides
    fun provideTriggerDao(db: MemorialDatabase) = db.getTriggerDao()

    @Singleton
    @Provides
    fun provideTourProgressDao(db: MemorialDatabase) = db.getTourProgressDao()

    @Singleton
    @Provides
    fun provideGuideProgressDao(db: MemorialDatabase) = db.getGuideProgressDao()

    @Singleton
    @Provides
    fun provideNavigator() : Navigator = Navigator(
        startDestination = GreetingDestination.GreetingScreen,
        type = NavigatorType.SINGLETOP,
        navigationGraph = { rootNavGraph() }
    )

    @Singleton
    @Provides
    fun provideSettingsRepository(
        ioDispatcher: CoroutineDispatcher,
        db : MemorialDatabase,
        @ApplicationContext context: Context,
    ) = SettingsRepository (ioDispatcher, db, context)

    @Singleton
    @Provides
    fun provideManagementTourRepository(
        dispatcher: CoroutineDispatcher,
        tourDao: TourDao,
        managementDao: ManagementDao,
    ) = ManagementRepository(dispatcher, managementDao, tourDao.asManageable())

    @Singleton
    @Provides
    fun provideManagementTourProgressRepository(
        dispatcher: CoroutineDispatcher,
        tourProgressDao: TourProgressDao,
        managementDao: ManagementDao,
    ) = ManagementRepository(dispatcher, managementDao, tourProgressDao.asManageable())

    @Singleton
    @Provides
    fun provideTourRepository(
        ioDispatcher: CoroutineDispatcher,
        tourDao: TourDao,
        guideDao: GuideDao,
        management: ManagementRepository<Tour, ManageableDao<Tour>>,
    ) = TourRepository(ioDispatcher, tourDao, guideDao, management)

    @Singleton
    @Provides
    fun provideGuideRepository(
        ioDispatcher: CoroutineDispatcher,
        guideDao: GuideDao,
        poiDao: PoiDao,
        triggerDao: TriggerDao,
    ) = GuideRepository(ioDispatcher, guideDao, poiDao, triggerDao)

    @Singleton
    @Provides
    fun provideTourPerformingRepository(
        ioDispatcher: CoroutineDispatcher,
        tourProgressDao: TourProgressDao,
        guideProgressDao: GuideProgressDao,
        management: ManagementRepository<TourProgress, ManageableDao<TourProgress>>,
    ) = PerformingRepository(ioDispatcher, tourProgressDao, guideProgressDao, management)

}