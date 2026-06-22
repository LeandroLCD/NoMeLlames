package cl.blipblipcode.prefixsapp.di

import androidx.annotation.Keep
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.IInsertAllowedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IInsertBlockedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockNonContactsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockPrivateNumbersUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetAllPrefixRulesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetSkipCallLogUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetSkipNotificationUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IMatchPrefixRuleUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.INormalizePhoneNumberUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher


@Keep
@EntryPoint
@InstallIn(SingletonComponent::class)
interface SpamCallPrefixEntryPoint {
    
    fun blockedCallRepository(): BlockedCallRepository

    fun allowedCallRepository(): AllowedCallRepository

    fun skipNotificationUseCase(): IGetSkipNotificationUseCase

    fun skipCallLogUseCase(): IGetSkipCallLogUseCase

    fun matchPrefixRuleUseCase(): IMatchPrefixRuleUseCase

    fun normalizePhoneNumberUseCase(): INormalizePhoneNumberUseCase

    fun insertBlockedCallUseCase(): IInsertBlockedCallUseCase

    fun insertAllowedCallUseCase(): IInsertAllowedCallUseCase

    fun blockPrivateNumbersUseCase(): IGetBlockPrivateNumbersUseCase

    fun blockNonContactsUseCase(): IGetBlockNonContactsUseCase

    fun contactsRepository(): ContactsRepository

    fun getAllPrefixRulesUseCase(): IGetAllPrefixRulesUseCase
}