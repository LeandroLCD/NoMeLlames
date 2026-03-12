package dev.andresfelipecaicedo.nomellames.domain.useCase.history

import dev.andresfelipecaicedo.nomellames.domain.model.HistoryItem
import dev.andresfelipecaicedo.nomellames.domain.repositories.AllowedCallRepository
import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCallHistoryUseCase @Inject constructor(
    private val blockedCallRepository: BlockedCallRepository,
    private val allowedCallRepository: AllowedCallRepository
) : IGetCallHistoryUseCase {
    
    override fun invoke(filter: IGetCallHistoryUseCase.HistoryFilter): Flow<List<HistoryItem>> {
        return combine(
            blockedCallRepository.getAllBlockedCalls(),
            allowedCallRepository.getAllAllowedCalls()
        ) { blockedCalls, allowedCalls ->
            val blockedItems = blockedCalls.map { call ->
                HistoryItem(
                    id = call.id,
                    phoneNumber = call.phoneNumber,
                    timestamp = call.timestamp,
                    type = HistoryItem.CallType.BLOCKED,
                    matchedPrefix = call.matchedPrefix
                )
            }
            
            val allowedItems = allowedCalls.map { call ->
                HistoryItem(
                    id = call.id,
                    phoneNumber = call.phoneNumber,
                    timestamp = call.timestamp,
                    type = HistoryItem.CallType.ALLOWED
                )
            }
            
            val allItems = when (filter) {
                IGetCallHistoryUseCase.HistoryFilter.ALL -> blockedItems + allowedItems
                IGetCallHistoryUseCase.HistoryFilter.BLOCKED -> blockedItems
                IGetCallHistoryUseCase.HistoryFilter.ALLOWED -> allowedItems
            }
            
            allItems.sortedByDescending { it.timestamp }
        }
    }
}

