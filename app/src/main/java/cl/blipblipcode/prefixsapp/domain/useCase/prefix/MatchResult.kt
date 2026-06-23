package cl.blipblipcode.prefixsapp.domain.useCase.prefix

sealed class MatchResult {
    data class Blocked(val prefix: String) : MatchResult()
    data class Allowed(val prefix: String) : MatchResult()
    data object NoMatch : MatchResult()
}
