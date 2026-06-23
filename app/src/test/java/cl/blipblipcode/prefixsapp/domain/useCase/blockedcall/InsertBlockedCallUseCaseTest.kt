package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InsertBlockedCallUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IInsertBlockedCallUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = InsertBlockedCallUseCase(repository)
    }

    @Test
    fun should_call_repository_with_phone_and_prefix_block_type_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val blockType = BlockType.Prefix("+57")

        //WHEN
        useCase(phoneNumber, blockType)

        //THEN
        assertEquals(phoneNumber, repository.lastInsertedPhone)
        assertEquals(blockType, repository.lastInsertedBlockType)
        assertEquals(1, repository.insertCallCount)
    }

    @Test
    fun should_propagate_private_number_block_type_to_repository_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val blockType = BlockType.PrivateNumber

        //WHEN
        useCase(phoneNumber, blockType)

        //THEN
        assertEquals(phoneNumber, repository.lastInsertedPhone)
        assertEquals(blockType, repository.lastInsertedBlockType)
    }

    @Test
    fun should_propagate_non_contact_block_type_to_repository_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val blockType = BlockType.NonContact

        //WHEN
        useCase(phoneNumber, blockType)

        //THEN
        assertEquals(phoneNumber, repository.lastInsertedPhone)
        assertEquals(blockType, repository.lastInsertedBlockType)
    }

    @Test
    fun should_call_repository_once_per_invocation_in_invoke() = runTest {
        //WHEN
        useCase("+573001234567", BlockType.Prefix("+57"))
        useCase("+573001234568", BlockType.Prefix("+57"))

        //THEN
        assertEquals(2, repository.insertCallCount)
    }
}