package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.model.PrefixRule
import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPrefixRulesUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IGetAllPrefixRulesUseCase {
    
    override fun invoke(): Flow<List<PrefixRule>> {
        return prefixRepository.getAllPrefixRules()
    }
}

