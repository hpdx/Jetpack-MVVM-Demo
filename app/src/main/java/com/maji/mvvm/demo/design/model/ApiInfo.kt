package com.maji.mvvm.demo.design.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 12:03.
 *
 * @author android_ls
 * @version 1.0
 */
@Entity
data class ApiInfo(val url: String, val content: String, val time: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}