package com.plcoding.stockmarketapp.presentation.company_listing

sealed class CompanyListingEvents {
    object Refresh:CompanyListingEvents()
    data class OnSearchQueryChange(val query: String): CompanyListingEvents()
}