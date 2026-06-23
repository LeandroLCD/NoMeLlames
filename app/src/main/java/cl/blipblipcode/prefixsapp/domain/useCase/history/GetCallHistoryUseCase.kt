package cl.blipblipcode.prefixsapp.domain.useCase.history

import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.HistoryItem
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
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
                    blockType = call.blockType,
                    matchedPrefix = call.matchedPrefix.takeIf { call.blockType is BlockType.Prefix }
                )
            }

            val allowedItems = allowedCalls.map { call ->
                HistoryItem(
                    id = call.id,
                    phoneNumber = call.phoneNumber,
                    timestamp = call.timestamp,
                    type = HistoryItem.CallType.ALLOWED,
                    blockType = BlockType.Allow
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