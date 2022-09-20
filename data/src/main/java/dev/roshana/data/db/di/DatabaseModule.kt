package dev.roshana.data.db.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
     public fun provideDb(app: Application): AppDatabase {
        return AppDatabase.getInstance(app)
    }
}