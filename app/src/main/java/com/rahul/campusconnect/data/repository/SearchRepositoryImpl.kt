package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.domain.model.SearchResult
import com.rahul.campusconnect.domain.model.SearchResultType
import com.rahul.campusconnect.domain.repository.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val eventRepository: EventRepository,
    private val placementRepository: PlacementRepository,
    private val notesRepository: NotesRepository,
    private val lostFoundRepository: LostFoundRepository
) : SearchRepository {

    override suspend fun search(query: String): Result<List<SearchResult>> = coroutineScope {
        try {
            val q = query.lowercase()

            val announcementsDeferred = async { announcementRepository.getAnnouncements() }
            val eventsDeferred = async { eventRepository.getAllEvents() }
            val placementsDeferred = async { placementRepository.getPlacements() }
            val notesDeferred = async { notesRepository.getNotes() }
            val lostFoundDeferred = async { lostFoundRepository.getItems() }

            val results = mutableListOf<SearchResult>()

            announcementsDeferred.await().getOrNull()?.filter {
                it.title.lowercase().contains(q) || it.description.lowercase().contains(q)
            }?.forEach {
                results.add(SearchResult(it.id, it.title, it.category, SearchResultType.ANNOUNCEMENT))
            }

            eventsDeferred.await().getOrNull()?.filter {
                it.title.lowercase().contains(q) || it.description.lowercase().contains(q) || it.venue.lowercase().contains(q)
            }?.forEach {
                results.add(SearchResult(it.id, it.title, "${it.category} • ${it.venue}", SearchResultType.EVENT))
            }

            placementsDeferred.await().getOrNull()?.filter {
                it.companyName.lowercase().contains(q) || it.jobRole.lowercase().contains(q)
            }?.forEach {
                results.add(SearchResult(it.id, it.companyName, "${it.jobRole} • ${it.packageLpa}", SearchResultType.PLACEMENT))
            }

            notesDeferred.await().getOrNull()?.filter {
                it.title.lowercase().contains(q) || it.subject.lowercase().contains(q)
            }?.forEach {
                results.add(SearchResult(it.id, it.title, "${it.subject} • ${it.uploadedByName}", SearchResultType.NOTE))
            }

            lostFoundDeferred.await().getOrNull()?.filter {
                it.title.lowercase().contains(q) || it.location.lowercase().contains(q)
            }?.forEach {
                results.add(SearchResult(it.id, it.title, "${it.type} • ${it.location}", SearchResultType.LOST_FOUND))
            }

            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
