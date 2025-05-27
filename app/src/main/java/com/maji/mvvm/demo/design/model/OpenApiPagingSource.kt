package com.maji.mvvm.demo.design.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.elvishew.xlog.XLog
import com.maji.mvvm.demo.base.model.HttpResult
import com.maji.mvvm.demo.base.model.getOrThrow
import com.maji.mvvm.demo.repository.GithubApiRepo

/**
 *
 * <p>
 * Created by android_ls on 2023/4/3 10:46.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiPagingSource : PagingSource<Int, ItemInfo>() {

    private val mRepository by lazy { GithubApiRepo() }

    override fun getRefreshKey(state: PagingState<Int, ItemInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemInfo> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val result: HttpResult<MutableMap<String, String>> =
                mRepository.getOpenApiList(nextPageNumber, 20)
            try {
                val resultData = result.getOrThrow()
                XLog.d("-->resultData.size = ${resultData.size}")
                if (resultData.isNotEmpty()) {
                    val apiList = mutableListOf<ItemInfo>()
                    for ((name, url) in resultData) {
                        apiList.add(ItemInfo(name, url))
                    }

                    XLog.d("-->apiList.size = ${apiList.size}")
                    LoadResult.Page(
                        data = apiList,
                        prevKey = null, // Only paging forward.
                        nextKey = nextPageNumber + 1
                    )
                }
            } catch (e: Exception) {
            }

            LoadResult.Page(emptyList(), null, -1)
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            LoadResult.Error(e)
        }
    }

}