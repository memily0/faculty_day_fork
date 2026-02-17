import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class Log(
    val dt: LocalDateTime,
    val id: Int,
    val status: String
)
val regA = Regex("""(\d{4}-\d{2}-\d{2})\s+(\d{2}:\d{2}).*ID\s*:\s*(\d+).*STATUS\s*:\s*(sent|delivered)""")

val regB = Regex("""TS\s*=\s*(\d{2}/\d{2}/\d{4})-(\d{2}:\d{2}).*status\s*=\s*(sent|delivered).*\#(\d+)""")

val regC = Regex("""\[(\d{2}\.\d{2}\.\d{4})\s+(\d{2}:\d{2})].*(sent|delivered).*\(id\s*:\s*(\d+)""")

fun normalize(line: String): Log? {
    val s = line.trim()

    regA.find(s)?.let {
        val (d, t, id, st) = it.destructured
        val dt = LocalDateTime.parse(
            "$d $t",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )
        return Log(dt, id.toInt(), st.lowercase())
    }

    regB.find(s)?.let {
        val (d, t, st, id) = it.destructured
        val dt = LocalDateTime.parse(
            "$d $t",
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        )
        return Log(dt, id.toInt(), st.lowercase())
    }

    regC.find(s)?.let {
        val (d, t, st, id) = it.destructured
        val dt = LocalDateTime.parse(
            "$d $t",
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        )
        return Log(dt, id.toInt(), st.lowercase())
    }

    return null


}

val logs = listOf(
    "2026-01-22 09:14 | ID:042 | STATUS:sent",
    "TS=22/01/2026-09:27; status=delivered; #042",
    "2026-01-22 09:10 | ID:043 | STATUS:sent",
    "2026-01-22 09:18 | ID:043 | STATUS:delivered",
    "TS=22/01/2026-09:05; status=sent; #044",
    "[22.01.2026 09:40] delivered (id:044)",
    "2026-01-22 09:20 | ID:045 | STATUS:sent",
    "[22.01.2026 09:33] delivered (id:045)",
    "   ts=22/01/2026-09:50; STATUS=Sent; #046   ",
    " [22.01.2026 10:05]   DELIVERED   (ID:046) "
)

val parsed = logs.mapNotNull { normalize(it) }


fun main(){
    val groupedVal = parsed.groupBy { it.id }

    val deliveries = mutableListOf<Pair<Int, Long>>()
    val incomplete = mutableListOf<Int>()
    val errors = mutableListOf<Int>()
    val violators = mutableListOf<Pair<Int, Long>>()

    for ((id, records) in groupedVal) {
        val sent = records.find { it.status == "sent" }
        val delivered = records.find { it.status == "delivered" }

        if (sent == null || delivered == null) {
            incomplete.add(id)
            continue
        }

        val minutes = ChronoUnit.MINUTES.between(sent.dt, delivered.dt)

        if (minutes < 0) {
            errors.add(id)
            continue
        }

        deliveries.add(id to minutes)

        if (minutes > 20) {
            violators.add(id to minutes)
        }
    }

    val sortedDeliveries = deliveries.sortedByDescending { it.second }
    println("Все посылки по длительности доставки (по убыванию):")
    sortedDeliveries.forEach { println("ID ${it.first} — ${it.second} минут") }

    val longest = sortedDeliveries.firstOrNull()
    if (longest != null) {
        println("Самый долгий заказ: ID ${longest.first} — ${longest.second} минут")
    }

    println("Нарушители правила (доставка >20 минут):")
    violators.forEach { println("ID ${it.first} — ${it.second} минут") }

    println("Неполные записи (нет sent или delivered): $incomplete")
    println("Ошибки времени (delivered раньше sent): $errors")

}