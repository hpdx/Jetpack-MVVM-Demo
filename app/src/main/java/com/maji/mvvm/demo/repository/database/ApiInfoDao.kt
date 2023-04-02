package com.maji.mvvm.demo.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.maji.mvvm.demo.design.model.ApiInfo

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
    fun loadApiList(): MutableList<ApiInfo>

    /**
     * 获取最后一次的调用结果
     */
    @Query("SELECT * FROM ApiInfo ORDER BY id DESC LIMIT 1")
    fun getLastApiInfo(): ApiInfo?

}