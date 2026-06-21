package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.model.BlockType

interface IInsertBlockedCallUseCase {
    suspend operator fun invoke(phoneNumber: String, blockType: BlockType)
}