package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.SearchResult

interface SearchRepository {
    suspend fun search(query: String): Result<List<SearchResult>>
}
