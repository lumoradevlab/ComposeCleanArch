package dev.roshana.data.db.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.data.db.AppDatabase
import dev.roshana.data.preference.Preference
import dev.roshana.data.preference.PreferenceImpl
import javax.inject.Singleton

/** PariSa;
coding and smoking ;)
 **/

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDatabase {
        return AppDatabase.getInstance(app)
    }

    @Provides
    fun providePreference(app: Application): Preference {
        return PreferenceImpl(app)
    }
}