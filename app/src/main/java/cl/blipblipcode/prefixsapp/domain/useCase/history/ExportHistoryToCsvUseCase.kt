package cl.blipblipcode.prefixsapp.domain.useCase.history

import android.os.Environment
import cl.blipblipcode.prefixsapp.domain.model.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExportHistoryToCsvUseCase @Inject constructor() : IExportHistoryToCsvUseCase {
    
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    override suspend fun invoke(history: List<HistoryItem>): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val csvContent = buildString {
                // Header
                appendLine("ID,Phone Number,Date Time,Type,Matched Prefix")
                
                // Data rows
                history.forEach { item ->
                    val formattedDate = dateTimeFormat.format(Date(item.timestamp))
                    val type = if (item.isBlocked) "BLOCKED" else "ALLOWED"
                    val prefix = item.matchedPrefix ?: ""
                    
                    appendLine("${item.id},\"${item.phoneNumber}\",\"$formattedDate\",$type,\"$prefix\"")
                }
            }
            
            val fileName = "call_history_${fileNameFormat.format(Date())}.csv"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            
            val file = File(downloadsDir, fileName)
            file.writeText(csvContent)
            
            file.absolutePath
        }
    }
}
