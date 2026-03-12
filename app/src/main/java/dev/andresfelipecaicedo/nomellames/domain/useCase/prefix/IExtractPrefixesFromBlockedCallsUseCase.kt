package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

/**
 * Use case interface for extracting prefixes from a list of phone numbers.
 * Filters out prefixes that already exist in the database.
 */
interface IExtractPrefixesFromBlockedCallsUseCase {
    /**
     * Extracts unique prefixes from a list of phone numbers.
     * 
     * @param phoneNumbers List of phone numbers to extract prefixes from
     * @return Result containing a list of new prefixes that don't exist yet
     */
    suspend operator fun invoke(phoneNumbers: List<String>): Result<List<String>>
}

