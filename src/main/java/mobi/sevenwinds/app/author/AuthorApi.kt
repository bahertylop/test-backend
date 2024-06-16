package mobi.sevenwinds.app.author

import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import mobi.sevenwinds.app.author.AuthorService
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

fun NormalOpenAPIRoute.author() {
    route("/author") {
        route("/add").post<Unit, AuthorRecord, AuthorRecord>(info("Добавить запись в таблицу авторов")) { param, body ->
            respond(AuthorService.addAuthorRecord(body, LocalDateTime.now()))
        }
    }
}

data class AuthorRecord(
    @NotNull val fullName: String,
)