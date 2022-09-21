package dev.roshana.jetpackcomposebasearch.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** PariSa;
coding and smoking ;)
 **/

@Module
@InstallIn(SingletonComponent::class)
class AppModule() {

    @Provides
    @Singleton
    fun providesContext(@ApplicationContext context: Context) = context.applicationContext
}