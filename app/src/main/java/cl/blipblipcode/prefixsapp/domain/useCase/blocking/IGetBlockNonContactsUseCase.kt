package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import kotlinx.coroutines.flow.Flow

interface IGetBlockNonContactsUseCase {
    operator fun invoke(): Flow<Boolean>
}