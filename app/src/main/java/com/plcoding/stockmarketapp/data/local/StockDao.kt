package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import retrofit2.http.Query

@Dao
interface StockDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListing(companyListingEntity: List<CompanyListingEntity>)


    @androidx.room.Query("DELETE FROM companyListingEntity")
    suspend fun clearCompanyListing()


    @androidx.room.Query(
        """ SELECT * FROM companylistingentity 
                        WHERE LOWER(name) LIKE '%' || LOWER(:query) ||
                        '%' OR UPPER(:query) == symbol """
    )
    suspend fun searchCompanyListing(query: String): List<CompanyListingEntity>
}