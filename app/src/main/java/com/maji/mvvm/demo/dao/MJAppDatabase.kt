package com.maji.mvvm.demo.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.maji.mvvm.demo.MJApp
import com.maji.mvvm.demo.main.model.ApiInfo

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 12:17.
 *
 * @author android_ls
 * @version 1.0
 */
@Database(version = 1, entities = [ApiInfo::class])
abstract class MJAppDatabase : RoomDatabase() {

    abstract fun apiInfoDao(): ApiInfoDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val sql = "alter table ApiInfo add column remark text not null default 'unknown'"
                database.execSQL(sql)
            }
        }

        private var instance: MJAppDatabase? = null

        @Synchronized
        fun getDatabase(): MJAppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                MJApp.context,
                MJAppDatabase::class.java,
                "mj_database"
            )
                // .addMigrations(MIGRATION_1_2) // 数据库版本升级
                .build().apply {
                    instance = this
                }
        }
    }

}