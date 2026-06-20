package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCachedPrefixRulesUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository,
    private val scope: CoroutineScope
) : IGetCachedPrefixRulesUseCase {

    @Volatile private var cached: List<PrefixRule>? = null
    @Volatile private var loadJob: Job? = null

    override fun invoke(): List<PrefixRule> {
        cached?.let { return it }
        loadIfNeeded()
        return cached ?: emptyList()
    }

    private fun loadIfNeeded() {
        synchronized(this) {
            if (cached != null) return
            val current = loadJob
            if (current != null && current.isActive) return
            loadJob = scope.launch {
                try {
                    val rules = prefixRepository.getAllPrefixRules().first()
                    cached = rules
                } catch (e: Exception) {
                    Timber.e(e, "Error loading prefix rules")
                }
            }
        }
    }
}
