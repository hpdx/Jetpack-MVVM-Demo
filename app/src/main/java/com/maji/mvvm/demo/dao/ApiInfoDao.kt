package com.maji.mvvm.demo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.maji.mvvm.demo.main.model.ApiInfo

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 12:14.
 *
 * @author android_ls
 * @version 1.0
 */
@Dao
interface ApiInfoDao {

    @Insert
    fun save(apiInfo: ApiInfo): Long

    @Query("SELECT * FROM ApiInfo ORDER BY id DESC LIMIT 0,20")
    fun loadApiList(): List<ApiInfo>

}