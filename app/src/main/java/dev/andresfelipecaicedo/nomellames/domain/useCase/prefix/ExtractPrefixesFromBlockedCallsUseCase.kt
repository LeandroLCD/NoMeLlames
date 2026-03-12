package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class ExtractPrefixesFromBlockedCallsUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IExtractPrefixesFromBlockedCallsUseCase {

    override suspend fun invoke(phoneNumbers: List<String>): Result<List<String>> {
        return prefixRepository.extractNewPrefixesFromNumbers(phoneNumbers)
    }
}

