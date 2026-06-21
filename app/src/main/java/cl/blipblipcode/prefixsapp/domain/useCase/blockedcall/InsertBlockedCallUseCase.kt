package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class InsertBlockedCallUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IInsertBlockedCallUseCase {

    override suspend operator fun invoke(phoneNumber: String, blockType: BlockType) {
        repository.insertBlockedCall(phoneNumber, blockType)
    }
}