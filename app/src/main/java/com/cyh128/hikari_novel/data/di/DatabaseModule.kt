package com.cyh128.hikari_novel.data.di

import android.content.Context
import androidx.room.Room
import com.cyh128.hikari_novel.data.source.local.database.read_history.horizontal_read_history.HorizontalReadHistoryDatabase
import com.cyh128.hikari_novel.data.source.local.database.read_history.vertical_read_history.VerticalReadHistoryDatabase
import com.cyh128.hikari_novel.data.source.local.database.search_history.SearchHistoryDatabase
import com.cyh128.hikari_novel.data.source.local.database.visit_history.VisitHistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun horizontalReadHistoryDatabase(@ApplicationContext context: Context): HorizontalReadHistoryDatabase {
        return Room.databaseBuilder(
            context,
            HorizontalReadHistoryDatabase::class.java,
            "horizontal_read_history" //名称不要修改，否则数据会丢失
        ).build()
    }

    @Provides
    fun horizontalReadHistoryDao(database: HorizontalReadHistoryDatabase) =
        database.horizontalReadHistoryDao()

    @Provides
    @Singleton
    fun searchHistoryDatabase(@ApplicationContext context: Context): SearchHistoryDatabase {
        return Room.databaseBuilder(
            context,
            SearchHistoryDatabase::class.java,
            "search_history" //名称不要修改，否则数据会丢失
        ).build()
    }

    @Provides
    fun searchHistoryDao(database: SearchHistoryDatabase) = database.searchHistoryDao()

    @Provides
    @Singleton
    fun verticalReadHistoryDatabase(@ApplicationContext context: Context): VerticalReadHistoryDatabase {
        return Room.databaseBuilder(
            context,
            VerticalReadHistoryDatabase::class.java,
            "vertical_read_history" //名称不要修改，否则数据会丢失
        ).build()
    }

    @Provides
    fun verticalReadHistoryDao(database: VerticalReadHistoryDatabase) =
        database.verticalReadHistoryDao()

    @Provides
    @Singleton
    fun visitHistoryDatabase(@ApplicationContext context: Context): VisitHistoryDatabase {
        return Room.databaseBuilder(
            context,
            VisitHistoryDatabase::class.java,
            "visit_history"  //名称不要修改，否则数据会丢失
        ).build()
    }

    @Provides
    fun visitHistoryDao(database: VisitHistoryDatabase) = database.visitHistoryDao()
}