package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable
                .select { BudgetTable.year eq param.year }

            val total = query.count()
            val sumByType = BudgetEntity.wrapRows(query).map { it.toResponse() }
                .groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            val data = BudgetEntity.wrapRows(query.limit(param.limit, param.offset)).map { it.toResponseStats() }

            val dataNew = if (!param.authorName.isNullOrBlank()) {
                data
                    .filter {it.authorName?.toLowerCase()?.contains(param.authorName.toLowerCase()) ?: false
                    }
            } else {
                data
            }

            val sortedData =
                dataNew.sortedByDescending { it.amount }.sortedWith(compareBy { it.month })

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = sortedData
            )
        }
    }
}