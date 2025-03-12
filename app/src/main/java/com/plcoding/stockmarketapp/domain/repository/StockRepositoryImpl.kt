package com.plcoding.stockmarketapp.domain.repository

import coil.network.HttpException
import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.mapper.toCompanyListing
import com.plcoding.stockmarketapp.data.remote.dto.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
) : StockRepository {

    private val dao = db.dao
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListing = try {
                val response = api.getListings()
                companyListingParser.parse(response.byteStream())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data"))
                null
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data"))
                null
            }

            //Getting data from the Cache
            remoteListing?.let { listings ->
                dao.clearCompanyListing()
                dao.insertCompanyListing(
                    listings.map { it.toCompanyListing() }
                )
                emit(Resource.Success(
                    data = dao
                        .searchCompanyListing("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try{

            val response = api.getIntradayInfo(symbol)
            val results = intradayInfoParser.parse(response.byteStream())
            return Resource.Success(results)
        }catch (e:IOException){
            Resource.Error(
                message = "Couldn't load intraday info!"
            )
        }
        catch (e:HttpException){
            Resource.Error(
                message = "Couldn't load intraday info!"
            )
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try{

            val result = api.getCompanyInfo(symbol)
            return Resource.Success(result.toCompanyInfo())
        }catch (e:IOException){
            Resource.Error(
                message = "Couldn't load company info!"
            )
        }
        catch (e:HttpException){
            Resource.Error(
                message = "Couldn't load company info!"
            )
        }
    }
}