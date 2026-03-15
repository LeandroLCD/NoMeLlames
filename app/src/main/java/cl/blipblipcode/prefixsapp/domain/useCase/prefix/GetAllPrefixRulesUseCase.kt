package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPrefixRulesUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IGetAllPrefixRulesUseCase {
    
    override fun invoke(): Flow<List<PrefixRule>> {
        return prefixRepository.getAllPrefixRules()
    }
}

