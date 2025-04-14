package com.ludian.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ludian.api.UserDataApi
import com.ludian.models.BookTestModel
import okhttp3.Response
import okio.IOException
import retrofit2.HttpException



class UserBookingRepository constructor(private val api: UserDataApi) : PagingSource<Int,BookTestModel>() {

    private val DEFAULT_PAGE_INDEX= 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookTestModel> {
     val page = params.key ?: DEFAULT_PAGE_INDEX
        return try {
           val response = api.getBookDataApi(page,params.loadSize)
            LoadResult.Page(response,
                prevKey = if(page == DEFAULT_PAGE_INDEX) null else page-1,
                nextKey = if(response.isEmpty()) null else page+1
               )
        }catch (e:IOException){
           LoadResult.Error(e)
        }catch (e:HttpException){
            LoadResult.Error(e)

        }
    }


    override fun getRefreshKey(state: PagingState<Int, BookTestModel>): Int? {
       return null
    }

}