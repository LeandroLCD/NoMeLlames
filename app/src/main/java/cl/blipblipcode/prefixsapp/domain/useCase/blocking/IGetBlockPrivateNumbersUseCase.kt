package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import kotlinx.coroutines.flow.Flow

interface IGetBlockPrivateNumbersUseCase {
    operator fun invoke(): Flow<Boolean>
}