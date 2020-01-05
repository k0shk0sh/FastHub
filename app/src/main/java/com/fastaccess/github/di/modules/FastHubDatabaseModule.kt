package com.fastaccess.github.di.modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.fastaccess.data.persistence.db.DATABASE_NAME
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.persistence.db.LOGIN_DATABASE_NAME
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.fasthub.dagger.annotations.ForApplication
import com.fastaccess.fasthub.dagger.annotations.ForDB
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import java.lang.reflect.Modifier
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module
class FastHubDatabaseModule {

    @Singleton @Provides fun provideDatabase(@ForApplication context: Context): FastHubDatabase = Room.databaseBuilder(
        context,
        FastHubDatabase::class.java, DATABASE_NAME
    )
        .allowMainThreadQueries() // allow querying on MainThread (this useful in some cases)
        .fallbackToDestructiveMigration() //  this mean that it will delete all tables and recreate them after version is changed
        .build()

    @Singleton @Provides fun provideLoginDatabase(@ForApplication context: Context): FastHubLoginDatabase = Room.databaseBuilder(
        context,
        FastHubLoginDatabase::class.java, LOGIN_DATABASE_NAME
    )
        .allowMainThreadQueries() // allow querying on MainThread
        .build()

    @Singleton @Provides fun providePreference(app: Application): FastHubSharedPreference = FastHubSharedPreference(app)

    @ForDB @Singleton @Provides fun provideGson(): Gson = GsonBuilder()
        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

    @Singleton @Provides @Named(value = "github_trending") fun provideFastHubTrendingDataReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference.child("github_trending")
    }

}