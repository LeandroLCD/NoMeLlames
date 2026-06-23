package cl.blipblipcode.prefixsapp.domain.exception

class PrefixAlreadyExistsException(
    val existingPrefix: String,
    val existingRuleType: String
) : Throwable("The prefix '$existingPrefix' already exists as a type rule $existingRuleType")

